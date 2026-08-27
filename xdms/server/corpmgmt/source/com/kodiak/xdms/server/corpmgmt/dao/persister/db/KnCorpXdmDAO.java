/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpXdmDAO.java
 * Subsystem:  POC
 * <p/>
 * Name                 Date         Release
 * -------------------- ------------ -------------------------------------
 * Namita.P.Nair        20-01-2011      7.0
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
package com.kodiak.xdms.server.corpmgmt.dao.persister.db;


import com.couchbase.client.core.error.DocumentNotFoundException;

import com.kodiak.common.commdto.common.KnIdDetailsDTO;
import com.kodiak.common.commdto.common.KnModifiedIdDetailsListDTO;
import com.kodiak.common.commdto.common.KnXDMDeviceAddlInfoDTO;

import com.kodiak.common.commdto.common.*;

import com.kodiak.common.commdto.request.KnXDMGroupPropertyInfoDTO;
import com.kodiak.common.commdto.request.KnXDMOSMInfoRequestDTO;
import com.kodiak.common.commdto.response.KnCORPGroupStatsRespDTO;
import com.kodiak.common.commdto.response.KnDeviceDetailsDTO;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.dbmgr.KnDBConst;
import com.kodiak.dbmgr.KnDBManager;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBPersistenceException;
import com.kodiak.xdms.server.common.dao.persister.db.KnQueryMapper;
import com.kodiak.xdms.server.common.dao.persister.db.util.KnDbUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnTGSModeChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnAPNConfigDTO;
import com.kodiak.xdms.server.common.dto.common.KnSIPProxySvcConfigDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubscriberDTO;
import com.kodiak.xdms.server.common.resources.KnErrorCodes;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpGroupInfoUtil;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.couchbase.KnPTTSettingDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.couchbase.KnUserProfileDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.KnCorpDBTablesRegistry;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm.*;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnCorpGroupListInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.KnDocChangeListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.*;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.*;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnDAOSourceTypes;
import com.kodiak.xdms.server.corpmgmt.resources.KnOperationTypes;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import static com.kodiak.xdms.server.common.resources.KnConstants.USER_PROFILE_MGMT_BIT;
import static com.kodiak.xdms.server.corpmgmt.business.helper.KnCorpCommonInfoUtil.getDirectoryURI;
import static com.kodiak.xdms.server.corpmgmt.dao.persister.KnPersisterConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.*;

public class KnCorpXdmDAO implements ICorpXdmDAO {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpXdmDAO.class);

    //stores the pttServerId
    private String pttServerId;
    private static volatile KnQueryMapper queryMapper = KnQueryMapper.getInstance();

    /**
     * @param pttServerId
     */
    public KnCorpXdmDAO(String pttServerId) {
        this.pttServerId = pttServerId;
    }


    public Map<String, String> getVendorID(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getVendorID(KnCorpMdnListPersistDTO,KnPersisterTxn) ";
        knLogger.debug(methodName, mdnList);
        Map<String, String> vendorMdnMap;
        KnXDMThirdPartyAccountDAO thirdPartyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMThirdPartyAccountDAO(pttServerId);
        vendorMdnMap = thirdPartyDAO.retrieveVendorID(mdnList, persisterTxn);
        knLogger.debug(methodName, vendorMdnMap);
        return vendorMdnMap;
    }

    public KnTPVendorDetailsPersistDTO getVendorDetails(String vendorID, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getVendorDetails(vendorID,KnPersisterTxn) ";
        knLogger.debug(methodName, vendorID);
        KnTPVendorDetailsPersistDTO vendorDetails = null;
        KnXDMThirdPartyAccountDAO thirdPartyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMThirdPartyAccountDAO(pttServerId);
        vendorDetails = thirdPartyDAO.retrieveVendorDetails(vendorID, persisterTxn);
        knLogger.debug(methodName, vendorDetails);
        return vendorDetails;
    }


    public Collection<KnCorpSubscriberDTO> getPoCSubscribersInfo(KnCorpMdnListPersistDTO contactListDTO, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        final String methodName = "getPoCSubscribersInfo(KnCorpMdnListPersistDTO,KnPersisterTxn) ";
        knLogger.debug(methodName, "ENTRY: ");
        Collection<KnCorpSubscriberDTO> subsList;
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        subsList = subscriberTable.selectPocSubscriberInfo(contactListDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return subsList;
    }

    public Collection<KnCorpSubscriberDTO> getPoCSubscribersInfo(Collection<String> addedMdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getPoCSubscribersInfo(Collection<String>,KnPersisterTxn) ";
        knLogger.debug(methodName, "ENTRY: ");
        Collection<KnCorpSubscriberDTO> subsList;
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        subsList = subscriberTable.selectPocSubscriberInfo(addedMdnList, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return subsList;
    }

    public Collection<KnCorpSubscriberDTO> getExternalPoCSubscriberInfo(Collection<KnCorpSubscriberDTO> contactListDTO, int corpId,
                                                                        KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getExternalPoCSubscriberInfo(Collection<KnCorpSubscriberDTO>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        KnXDMExtPoCSubscriberDAO extPoCSubsDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(pttServerId);
        Collection<KnCorpSubscriberDTO> subscDTO =
                extPoCSubsDAO.selectExternalPoCSubscriberInfo(contactListDTO, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return subscDTO;
    }

    public Collection<String> getContactMDNs(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getContactMDNs(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        Collection<String> mdnList = null;
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        mdnList = subscriberTable.getContactMDNs(mdn, persisterTxn);
        knLogger.debug(methodName, "EXIT :");
        return mdnList;
    }


    public Collection<KnCorpSubscriberDTO> getSubscriberPrivateContactListInfo(KnCorpMdnListPersistDTO contactListDTO,
                                                                               int privatelistId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getSubscriberPrivateContactListInfo(KnCorpMdnListPersistDTO, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        KnXDMCorpListMemberDAO corpListMemDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        Collection<KnCorpSubscriberDTO> subscList =
                corpListMemDAO.getSubscriberPrivateContactListInfo(contactListDTO, privatelistId, persisterTxn);
        knLogger.debug(methodName, "EXIT.");
        return subscList;
    }

    public Collection<Integer> getSubsMappedSublistId(KnCorpSublistListPersistDTO sublistListDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getSubsMappedSublistId(KnCorpSublistListPersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListDistInfoDAO corpListDistDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListDistInfoDAO(pttServerId);
        Collection<Integer> sublistIdList = corpListDistDAO.getSubsMappedSublistId(sublistListDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT.");
        return sublistIdList;
    }


    public Collection<Integer> getPoCSublistIdInfo(Collection<Integer> sublistid, int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getPoCSublistIdInfo(Collection<Integer> , int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        KnXDMCorpListInfoDAO corpListDistDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        Collection<Integer> validPoCSublist = corpListDistDAO.getPoCSublistIdInfo(sublistid, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT.");
        return validPoCSublist;
    }

    public Collection<Integer> getCommonContactListRejectForGrp(Collection<Integer> sublistid, int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getCommonContactListRejectForGrp(Collection<Integer> , int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        KnXDMCorpListInfoDAO corpListDistDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        Collection<Integer> validPoCSublist = corpListDistDAO.getCommonContactListRejectForGrp(sublistid, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT.");
        return validPoCSublist;
    }

    public Map<Integer, Integer> getAllTypePoCSublistIdInfo(Collection<Integer> sublistid, int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getAllTypePoCSublistIdInfo(Collection<Integer> , int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        KnXDMCorpListInfoDAO corpListDistDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        Map<Integer, Integer> validPoCSubMap = corpListDistDAO.getAllTypePoCSublistIdInfo(sublistid, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT.");
        return validPoCSubMap;
    }

    public void addPrivateContactList(Collection<KnCorpSubscriberDTO> privateContactList, int privateSublistId,
                                      KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "addPrivateContactList(Collection<KnCorpSubscriberDTO>,int,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        KnXDMCorpListMemberDAO corpListMemberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        corpListMemberDAO.insertSublistMembers(privateContactList, privateSublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void addPrivateContactList(Collection<KnCorpSubscriberDTO> privateContactList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "addPrivateContactList(Collection<KnCorpSubscriberDTO>,int,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        KnXDMCorpListMemberDAO corpListMemberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        corpListMemberDAO.insertSublistMembers(privateContactList, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void deleteSublistMembers(Collection<String> contactList, int sublistId,
                                     KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteSublistMembers(Collection<String>,int,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListMemberDAO corpListMemberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        corpListMemberDAO.deleteSublistMembers(contactList, sublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void pushSublistToSubscriber(Collection<Integer> addedSubListId, KnCorpSubscriberDTO subsDTO,
                                        KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "pushSublistToSubscriber(Collection<Integer>,KnCorpSubscriberDTO,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListDistInfoDAO corpListDistInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListDistInfoDAO(pttServerId);
        corpListDistInfoDAO.pushSublistToSubscriber(addedSubListId, subsDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
    }

    public void removeSublistMappingForSubscriber(Collection<Integer> removeSubListId, KnCorpSubscriberDTO subsDTO,
                                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "removeSublistMappingForSubscriber(Collection<Integer>,KnCorpSubscriberDTO,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListDistInfoDAO corpListDistInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListDistInfoDAO(pttServerId);
        corpListDistInfoDAO.removeSublistMappingForSubscriber(removeSubListId, subsDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }


    public Map<String, KnOPDirChgDTO> updateSubcribersImpactedTables(Collection<String> mdnList, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException {
        final String methodName = "updateSubcribersImpactedTables(Collection<String>, KnPersisterTxn,  Map<String, KnOPDirChgDTO> )";
        knLogger.debug(methodName, "ENTRY :");

        // Move the below 3 operations to ASYNC
        KnXDMCorpResourceListIndexDocDAO resourceDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpResourceListIndexDocDAO(pttServerId);
        Map<String, KnOPDocChgDTO> currResourcEtag = resourceDAO.updateEtag(mdnList, persisterTxn);
        knLogger.debug(methodName, "currResourcEtagcurrResourc :", KnGDPRTemplate.mapKeyMdn(currResourcEtag));
        KnXDMDirectoryDAO directoryDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMDirectoryDAO(pttServerId);
        Map<String, KnOPDirChgDTO> currdirectoryEtag = directoryDAO.updateEtag(mdnList, persisterTxn, etagMap);
        knLogger.debug(methodName, "currdirectoryEtagcurrdirectoryEtag :", KnGDPRTemplate.mapKeyMdn(currdirectoryEtag));
        KnXDMSubscriberInfoDAO subscriberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        Map<String, KnCorpSubscriberDTO> subscrInfoMap = subscriberDAO.getSubsribersCorporateDetails(mdnList, persisterTxn);

        for (String mdn : currdirectoryEtag.keySet()) {
            Collection<KnOPDocChgDTO> docList = new ArrayList<KnOPDocChgDTO>();
            mdn = mdn.trim();
            if (currResourcEtag.get(mdn) != null) {
                docList.add(currResourcEtag.get(mdn));
                KnOPDirChgDTO directory = currdirectoryEtag.get(mdn);
                directory.setDocChgDTO(docList);
                KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
                if (subsc != null) {
                    directory.setPocHome(subsc.getPocHome());
                    directory.setPresenceHome(subsc.getPresenceHome());
                    directory.setNotfnCapability(subsc.isNotfnCapabiliy());
                    directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                    directory.setClientType(subsc.getClientType());
                    if (null != subsc.getUserProfileIndex()) {
                        directory.setNtfyOnAnyMDN(subsc.getUserProfileIndex() > 0 ? 1 : 0);
                    }
                }
            }
        }
        List<String> tempList = new ArrayList<>(mdnList);
        tempList.removeAll(currdirectoryEtag.keySet());
        knLogger.debug(methodName, "EXIT:Mismatched mdnList for whom DocDiff notification could not be sent", KnGDPRTemplate.mdnList(tempList));
        knLogger.debug(methodName, "currdirectoryEtagcurrdire :", KnGDPRTemplate.mapKeyMdn(currdirectoryEtag));
        return currdirectoryEtag;
    }

    public Map<String, KnOPDirChgDTO> updateSubcribersImpactedTablesForUpm(Collection<String> mdnList, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> currdirectoryEtag) throws KnDAOException {
        final String methodName = "updateSubcribersImpactedTablesForUpm(Collection<String>, KnPersisterTxn,  Map<String, KnOPDirChgDTO> )";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpResourceListIndexDocDAO resourceDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpResourceListIndexDocDAO(pttServerId);
        Map<String, KnOPDocChgDTO> currResourcEtag = resourceDAO.updateEtag(mdnList, persisterTxn);
        knLogger.debug(methodName, "currResourcEtagcurrResourc :", KnGDPRTemplate.mapKeyMdn(currResourcEtag));
        KnXDMDirectoryDAO directoryDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMDirectoryDAO(pttServerId);
        //Map<String, KnOPDirChgDTO> currdirectoryEtag = directoryDAO.updateEtag(mdnList, persisterTxn, etagMap);
        knLogger.debug(methodName, "currdirectoryEtagcurrdirectoryEtag :", KnGDPRTemplate.mapKeyMdn(currdirectoryEtag));
        KnXDMSubscriberInfoDAO subscriberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        Map<String, KnCorpSubscriberDTO> subscrInfoMap = subscriberDAO.getSubsribersCorporateDetails(mdnList, persisterTxn);

        for (String mdn : currdirectoryEtag.keySet()) {
            Collection<KnOPDocChgDTO> docList = new ArrayList<KnOPDocChgDTO>();
            mdn = mdn.trim();
            if (currResourcEtag.get(mdn) != null) {
                docList.add(currResourcEtag.get(mdn));
                KnOPDirChgDTO directory = currdirectoryEtag.get(mdn);
                directory.setDocChgDTO(docList);
                KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
                if (subsc != null) {
                    directory.setPocHome(subsc.getPocHome());
                    directory.setPresenceHome(subsc.getPresenceHome());
                    directory.setNotfnCapability(subsc.isNotfnCapabiliy());
                    directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                    directory.setClientType(subsc.getClientType());
                    if (null != subsc.getUserProfileIndex()) {
                        directory.setNtfyOnAnyMDN(subsc.getUserProfileIndex() > 0 ? 1 : 0);
                    }
                }
            }
        }
        List<String> tempList = new ArrayList<>(mdnList);
        tempList.removeAll(currdirectoryEtag.keySet());
        knLogger.debug(methodName, "EXIT:Mismatched mdnList for whom DocDiff notification could not be sent", KnGDPRTemplate.mdnList(tempList));
        knLogger.debug(methodName, "currdirectoryEtagcurrdire :", KnGDPRTemplate.mapKeyMdn(currdirectoryEtag));
        return currdirectoryEtag;
    }

    public Map<String, KnOPDirChgDTO> updateSubcribersImpactedTables(Collection<String> mdnList, Collection<KnCorpSubscriberDTO> mdnDTOList, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException {
        final String methodName = "updateSubcribersImpactedTables(Collection<String>, Collection<KnCorpSubscriberDTO>, KnPersisterTxn,  Map<String, KnOPDirChgDTO> )";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpResourceListIndexDocDAO resourceDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpResourceListIndexDocDAO(pttServerId);
        Map<String, KnOPDocChgDTO> currResourcEtag = resourceDAO.updateEtag(mdnList, persisterTxn);
        KnXDMDirectoryDAO directoryDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMDirectoryDAO(pttServerId);
        Map<String, KnOPDirChgDTO> currdirectoryEtag = directoryDAO.updateEtag(mdnList, persisterTxn, etagMap);
        for (KnCorpSubscriberDTO corpSubscriberDTO : mdnDTOList) {
            if (currdirectoryEtag.containsKey(corpSubscriberDTO.getMdn())) {
                Collection<KnOPDocChgDTO> docList = new ArrayList<KnOPDocChgDTO>();
                docList.add(currResourcEtag.get(corpSubscriberDTO.getMdn()));
                KnOPDirChgDTO directory = currdirectoryEtag.get(corpSubscriberDTO.getMdn());
                directory.setDocChgDTO(docList);
                directory.setPocHome(corpSubscriberDTO.getPocHome());
                directory.setPresenceHome(corpSubscriberDTO.getPresenceHome());
                directory.setNotfnCapability(corpSubscriberDTO.isNotfnCapabiliy());
                directory.setProtoVersion(Integer.toString(corpSubscriberDTO.getClientPVmajorVer()));
                directory.setClientType(corpSubscriberDTO.getClientType());
            }
        }
        List<String> tempList = new ArrayList<>(mdnList);
        tempList.removeAll(currdirectoryEtag.keySet());
        knLogger.debug(methodName, "Mismatched mdnList for whom DocDiff notification could not be sent ", KnGDPRTemplate.mdnList(tempList), "EXIT");
        return currdirectoryEtag;
    }

    public void updateSubscCorpListId(String mdn, int privateCorpListId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateSubscCorpListId(String,int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        subscriberTable.updateSubscCorpListId(mdn, privateCorpListId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
    }

    public Collection<String> getCorporateSpecificPoCSubscribersFrmList(Collection<String> mdnList,
                                                                        int corpId,
                                                                        KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorporateSpecificPoCSubscribersFrmList(Collection<String>,int,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        Collection<String> pocSubsc =
                subscriberTable.getCorporateSpecificPoCSubscribersFrmList(mdnList, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return pocSubsc;
    }

    public Map<Integer, Collection<String>> sublistPushedToSubscribersFrmList(KnIPCorpSublistSubscDistDTO distDTO,
                                                                              KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "sublistPushedToSubscribersFrmList(KnIPCorpSublistSubscDistDTO, KnPersisterTxn)";
        KnXDMCorpListDistInfoDAO corpListDistInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListDistInfoDAO(pttServerId);
        return corpListDistInfoDAO.selectSublistPushedFrmList(distDTO, persisterTxn);
    }

    public LinkedList<Integer> sublistPushedToSubscribers(String MDN, int corpId,
                                                          KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "sublistPushedToSubscribers(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListDistInfoDAO corpListDistInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListDistInfoDAO(pttServerId);
        LinkedList<Integer> sublistMapList = corpListDistInfoDAO.selectSublistPushed(MDN, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return sublistMapList;
    }

    public Map<Integer, Integer> sublistPushedToSubscribersAndCount(LinkedList<Integer> sublistIds,
                                                                    KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "sublistPushedToSubscribers(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListDistInfoDAO corpListDistInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListDistInfoDAO(pttServerId);
        Map<Integer, Integer> sublistMapList = corpListDistInfoDAO.sublistPushedToSubscribersAndCount(sublistIds, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return sublistMapList;
    }

    public Map<String, String> getAllSubscriberContactCount(Collection<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getAllSubscriberContactCount(Collection<String>,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpContactCountDAO corpContactCount =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactCountDAO(pttServerId);
        Map<String, String> subscContactCount = corpContactCount.getAllSubscriberContactCount(mdnList, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return subscContactCount;
    }


    public Map<String, String> getSublistPushedSublistCntForSubsc(KnIPCorpSublistSubscDistDTO subsRequestDTO,
                                                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSublistPushedSublistCntForSubsc(KnIPCorpSublistSubscDistDTO,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        KnXDMCorpListDistInfoDAO corpListDist =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListDistInfoDAO(pttServerId);
        Map<String, String> sublidtPushedCount = corpListDist.getSublistPushedSublistCntForSubsc(subsRequestDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return sublidtPushedCount;
    }

    public Map<String, Integer> getSubscriberAdditionalContactCnt(KnIPCorpSublistSubscDistDTO distDTO,
                                                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscriberAdditionalContactCnt(KnIPCorpSublistSubscDistDTO,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        KnXDMCorpListMemberDAO listMemberCnt =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        Map<String, Integer> addSubContactCnt = listMemberCnt.
                getSubscriberAdditionalContactCnt(distDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return addSubContactCnt;
    }

    public int getFinalMemberContactCount(Collection<Integer> finalSublistIds,
                                          Collection<KnCorpSubscriberDTO> mdnsToBeAddedToPrivateList,
                                          KnMdnDetailsPersistDTO contactMdnPersistDto,
                                          KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getFinalMemberContactCount(Collection<Integer>, Collection<KnCorpSubscriberDTO>," +
                "KnMdnDetailsPersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListMemberDAO corpListMenDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        int count = corpListMenDAO.getFinalMemberContactCount(finalSublistIds, mdnsToBeAddedToPrivateList,
                contactMdnPersistDto, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return count;
    }

    public void removeSubscribersSublist(KnIPCorpSublistSubscDistDTO subsRequestDTO,
                                         KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "removeSubscribersSublist(KnIPCorpSublistSubscDistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");

        KnXDMCorpListDistInfoDAO corpListDist =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListDistInfoDAO(pttServerId);
        corpListDist.removeSubscribersSublist(subsRequestDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    /**
     * This method returns the external contact in the corporation
     *
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, KnCorpSubscriberDTO> getCorpExternalSubscriber(int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMExtPoCSubscriberDAO extSubscriberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(pttServerId);
        return extSubscriberDAO.getCorpExternalSubscriber(corpId, persisterTxn);
    }

    public KnCorpContactBean getInternalSubscriberList(int corpId, Integer filterType, Integer fetchSize, Integer nextToken, Integer sortType,
                                                       boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getInternalSubscriberList(corpId, filterType, fetchSize, nextToken, sortType, readOnly, persisterTxn);
    }

    public Map<String, Map<String, Object>> getInternalSubscriberPackageMap(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getInternalSubscriberPackageMap(mdnList, persisterTxn);
    }

    /**
     * @param corpId       corporate id
     * @param filterType   possible value 4
     * @param fetchSize    number of Row to fetch
     * @param nextToken    the page number to fetch
     * @param sortType     By [1]MDN OR By [2]NAME, 0 means no sorting
     * @param persisterTxn
     * @return List of NNI Subscriber
     * @throws KnDAOException
     */
    @Override
    public KnCorpContactBean getInternalInteropSubscriberList(int corpId, Integer filterType,
                                                              Integer fetchSize, Integer nextToken, Integer sortType, boolean readOnly,
                                                              KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getInternalInteropSubscriberList(corpId, filterType, fetchSize, nextToken, sortType, readOnly, persisterTxn);
    }

    public Map<String, Map<String, KnCorpSubscriberDTO>> getCorpAllSubscribers(int corpId, int
            maxAllowedContactCount, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getCorpAllSubscribers(corpId, maxAllowedContactCount, persisterTxn);
    }

    public Set<Integer> getPamAccId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getPamAccId(corpId, persisterTxn);
    }

    public Map<String, Collection<KnCorpSubscriberDTO>> getSubscPrivateInternalExternalContacts(
            KnIPCorpContactDTO contactDTO, int corpListId, int maxContactLimit, boolean readOnly,
            KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscPrivateInternalExternalContacts(KnIPCorpContactDTO,int,int,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        Map<String, Collection<KnCorpSubscriberDTO>> contacts = pocSubscInfoDAO.getSubscPrivateInternalExternalContacts(contactDTO, corpListId, maxContactLimit, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return contacts;
    }

    public KnCorpSubscContactListRespDTO addExternalContacts(KnIPCorpContactListDTO contactListDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "addExternalContacts(KnIPCorpContactListDTO,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMExtPoCSubscriberDAO extPoCSubsc =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(pttServerId);
        KnCorpSubscContactListRespDTO response = extPoCSubsc.insertExternalPoCMemberDetails(contactListDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return response;
    }

    public KnCorpResponseDTO modifyExternalContacts(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "modifyExternalContacts(KnIPCorpContactDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMExtPoCSubscriberDAO extPoCSubsc =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(pttServerId);
        KnCorpResponseDTO response = extPoCSubsc.updateExternalPoCMemberDetails(contactDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return response;
    }

    public Collection<String> getSubscMdnsHavingExtContact(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getSubscMdnsHavingExtContact(KnIPCorpContactDTO,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMExtPoCSubscriberDAO extPoCSubsc =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(pttServerId);
        Collection<String> subsc = extPoCSubsc.getSubscMdnsHavingExtContact(contactDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return subsc;
    }

    public Collection<String> getPoCSubscPrsntForSublistFrmMdnList(Collection<String> memberList,
                                                                   int corpListId,
                                                                   KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getPoCSubscPrsntForSublistFrmMdnList(Collection<String>,int,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        KnXDMCorpListMemberDAO corpListMemberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        Collection<String> members =
                corpListMemberDAO.getPoCSubscPrsntForSublistFrmMdnList(memberList, corpListId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return members;
    }

    public Collection<KnCorpSubscriberDTO> getSublistContactList(int sublistId, boolean readOnly,
                                                                 KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSublistContactList(int,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "Entry ");
        KnXDMCorpListMemberDAO corpListMemDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        Collection<KnCorpSubscriberDTO> contactList = corpListMemDAO.getSublistContactList(sublistId, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return contactList;
    }

    public Collection<KnCorpSubscriberDTO> getSubscPrivateMemberListDetails(int privateContactListId,
                                                                            KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscPrivateMemberListDetails(int,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListMemberDAO corpListMemDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        Collection<KnCorpSubscriberDTO> contactList =
                corpListMemDAO.getSubscPrivateContactListDetails(privateContactListId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return contactList;
    }

    public void deleteSubscPrivateContactList(Collection<String> removePrivateContactList, int privateListId,
                                              KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteSubscPrivateContactList(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListMemberDAO corpListMemDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        corpListMemDAO.deleteSubscPrivateContactList(removePrivateContactList, privateListId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void addSublistMembers(Collection<KnCorpSubscriberDTO> finalMemberList, int sublistId,
                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "addSublistMembers(Collection<KnCorpSubscriberDTO>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        KnXDMCorpListMemberDAO corpListMemDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        corpListMemDAO.insertSublistMembers(finalMemberList, sublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public Collection<String> getSubscribersDistToSublist(int sublistId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscribersDistToSublist(int, boolean, KnPersisterTxn)";
        KnXDMCorpListDistInfoDAO corpListMemDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListDistInfoDAO(pttServerId);
        Collection<String> distributionList = corpListMemDAO.selectSubscribersDistToSublist(sublistId, readOnly, persisterTxn);
        return distributionList;
    }

    public LinkedHashSet<String> selectSubsDistributionToMultipleSublist(Collection<Integer> sublistIds, String MDN, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscribersDistToSublist(int,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListDistInfoDAO corpListMemDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListDistInfoDAO(pttServerId);
        LinkedHashSet<String> distributionList = corpListMemDAO.selectSubsDistributionToMultipleSublist(sublistIds, MDN, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return distributionList;
    }

    public Set<Integer> SelectCorpListIdOnlyPushedViaPrivateGroupList(Collection<Integer> sublistIds, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "SelectCorpListIdOnlyPushedViaPrivateGroupList(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListDistInfoDAO corpListMemDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListDistInfoDAO(pttServerId);
        Set<Integer> distributionList = corpListMemDAO.SelectCorpListIdOnlyPushedViaPrivateGroupList(sublistIds, mdn, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return distributionList;
    }

    public KnCorpContactListRespDTO getExternalContactDetails(Collection<KnIPCorpContactDTO> contactDTO, int corpId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getExternalContactDetails(Collection<KnIPCorpContactDTO>, int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnCorpContactListRespDTO respDTO;
        KnXDMExtPoCSubscriberDAO extPoCSubsc =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(pttServerId);
        respDTO = extPoCSubsc.selectExternalContactDetails(contactDTO, corpId, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return respDTO;
    }

    public void modifySublistName(String sublistName, int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "modifySublistName(String,int,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        corpListDAO.modifySublistName(sublistName, sublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void updateCorpPairedContListId(int corpId, int pairedListId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateCorpPairedContListId(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpInfoDAO corpInfo = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpInfoDAO(pttServerId);
        corpInfo.updateCorpPairedContactListId(corpId, pairedListId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
    }

    public void createSublist(KnCorpSublistDTO sublistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "createSublist(KnCorpSublistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        corpListDAO.insertCorpListInfo(sublistDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public Collection<KnCorpGroupInfoPersistDTO> getGroupsMappedToSublist(int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupsMappedToSublist(int,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        KnXDMCorpGroupListRefDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpCorpGroupListRefDAO(pttServerId);
        Collection<KnCorpGroupInfoPersistDTO> groupList = corpListDAO.selectGroupMappedToSublist(sublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return groupList;
    }

    public KnCorpSublistDTO getSublistInfo(int sublistId, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListInfoDAO corpListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListInfoDAO(pttServerId);
        return corpListDAO.getSublistInfo(sublistId, corpId, readOnly, persisterTxn);
    }

    public KnCorpSublistDTO getSublistInfoWithHierarchyId(int sublistId, int corpId, boolean readOnly, KnPersisterTxn persisterTxn,String hierarchyId) throws KnDAOException {
        KnXDMCorpListInfoDAO corpListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListInfoDAO(pttServerId);
        return corpListDAO.getSublistInfoWithHierarchyId(sublistId, corpId, readOnly, persisterTxn,hierarchyId);
    }

    public int getCommonContactListReject(int sublistId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListInfoDAO corpListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListInfoDAO(pttServerId);
        return corpListDAO.getCommonContactListReject(sublistId, corpId, persisterTxn);
    }

    public boolean isSublistExistInCorporate(int corpId, String sublistName, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "isSublistExistInCorporate(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListInfoDAO corpListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListInfoDAO(pttServerId);
        return corpListDAO.isSublistExistInCorporate(corpId, sublistName, persisterTxn);
    }

    public Collection<KnCorpSubscriberDTO> selectDistinctMembers(Collection<KnCorpSubscriberDTO> addedMemList, Collection<Integer>
            addedSublistIds, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {

        final String methodName = "selectDistinctMembers(Collection<KnCorpSubscriberDTO>, Collection<Integer>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMSubscriberInfoDAO subsInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        Collection<KnCorpSubscriberDTO> distinctMemberList = subsInfoDAO.selectDistinctSublistMembers(addedMemList,
                addedSublistIds, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return distinctMemberList;
    }

    public Collection<KnCorpSubscriberDTO> getDistinctMembersForSublist(Collection<String> addedMdnList,
                                                                        Collection<Integer> addedSublistIds,
                                                                        KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getDistinctMembersForSublist(Collection<String>, Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : addedMdnList ~ ", addedMdnList == null ? addedMdnList : KnGDPRTemplate.mdnList(addedMdnList), ", addedSublistIds ~ ", addedSublistIds);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ResultSet selectRs = null;
        String query = null;
        Collection<KnCorpSubscriberDTO> subscribersList = new ArrayList<KnCorpSubscriberDTO>();
        Map<String, KnCorpSubscriberDTO> subscribersMap = new HashMap<String, KnCorpSubscriberDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            Collection<String> mdnList = new ArrayList<String>();
            if (!isObjectNull(addedMdnList)) {
                mdnList.addAll(addedMdnList);
            }
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            if (addedSublistIds != null && !addedSublistIds.isEmpty()) {
                String selectquery = queryMapper.getQuery(SELECT_DISTINCT_SUBLIST_MEMBERS);
                selectquery = replaceContactWithValue(selectquery, SUBLISTID, formIntegerCommaSeperatedIdList(addedSublistIds));
                pstmt = conn.prepareStatement(selectquery);
                knLogger.debug(methodName, "Executing query - ", "'", selectquery, "'");
                selectRs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully.");

                while (selectRs.next()) {
                    KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                    String mdn = selectRs.getString(1).trim();
                    subscriberDTO.setMdn(mdn);
                    int mdnCorpId = selectRs.getInt(2);
                    subscriberDTO.setCorpId(mdnCorpId);
                    //subscribersList.add(subscriberDTO);
                    subscribersMap.put(mdn, subscriberDTO);
                }
                if (subscribersMap.values() != null) {
                    subscribersList.addAll(subscribersMap.values());
                }
            }
            if (mdnList != null && !mdnList.isEmpty()) {
                var mdnListArray = new ArrayList<>(mdnList);
                var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
                for (var subsList : subsLists) {
                    query = queryMapper.getQuery(GET_POC_SUBSCRIBER_INFO);
                    query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(subsList));
                    pstmt = conn.prepareStatement(query);
                    knLogger.debug(methodName, "Executing query", "'", query, "'");
                    rs = pstmt.executeQuery();
                    knLogger.debug(methodName, "Query executed successfully.");
                    while (rs.next()) {
                        KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                        String mdn = rs.getString(2).trim();
//                    if (subscribersMap.get(mdn) == null) {
                        subscriberDTO.setMdn(mdn);
                        int mdnCorpId = rs.getInt(1);
                        subscriberDTO.setCorpId(mdnCorpId);
                        //multilingual revert changes
                        if (rs.getString(3) != null) {
                            subscriberDTO.setName(new String(rs.getString(3).getBytes("8859_1"), "UTF-8"));
                        }
                        subscriberDTO.setClientType(rs.getInt(4));
                        subscribersList.add(subscriberDTO);
                        subscribersMap.put(mdn, subscriberDTO);
//                    }
                    }
                }
            }
            knLogger.debug(methodName, "Fetched the details from DB :  ", subscribersMap.values().size());
            return subscribersMap.values();

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the distinct members from DB - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching the distinct members from DB- ",
                    e);
            throw KnDbUtil.processException(e, "Failed while fetching the distinct members from DB" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeResultSet(selectRs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "addedMdnList ~ ", KnGDPRTemplate.mdnList(addedMdnList), ", addedSublistIds ~ ", addedSublistIds, "EXIT :Fetched the details from DB : ", subscribersMap.values().size());
        }
    }

    public int getCorpSublistCount(int corpId, int listType, boolean readOnly, KnPersisterTxn persisterTxn,String hierarchyId) throws KnDAOException {
        final String methodName = "getCorpSublistCount(int, int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        int count = corpListDAO.getCorpSublistCount(corpId, listType, readOnly, persisterTxn,hierarchyId);
        knLogger.debug(methodName, "EXIT");
        return count;
    }

    public Collection<KnCorpSublistDTO> getAllSublist(int corpId, int listType, int nextToken, int fetchSize, boolean readOnly, KnPersisterTxn persisterTxn, String hierarchyId) throws KnDAOException {
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        Collection<KnCorpSublistDTO> sublistLists = corpListDAO.selectAllSublist(corpId, listType, nextToken, fetchSize, readOnly, persisterTxn,hierarchyId);
        return sublistLists;
    }

    /**
     * Returns the sublist members with details in the response DTO.
     *
     * @param operationType
     * @param sublistId
     * @param corpId
     * @param maxContactLimit
     * @param persisterTxn
     * @return KnCorpSublistRespDTO
     * @throws KnDAOException
     */
    public KnCorpSublistRespDTO getSublistDetails(String operationType, int sublistId, int corpId, int maxContactLimit, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        final String methodName = "getSublistDetails(int, int, int,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :", "sublistId :", sublistId, " corpId :", corpId);
        KnCorpSublistRespDTO respDTO = new KnCorpSublistRespDTO();
        Map<String, List<String>> memberMap = getSublistMemMap(sublistId, corpId, readOnly, persisterTxn);
        List<String> intMemList = memberMap.get(KnConstants.INTERNAL);
        knLogger.debug(methodName, "intMemList size", intMemList.size());
        List<String> extMemList = memberMap.get(KnConstants.EXTERNAL);
        knLogger.debug(methodName, "extMemList size", extMemList.size());
        Collection<KnCorpContactDTO> memberList = new ArrayList<KnCorpContactDTO>(intMemList.size() + extMemList.size());
        if (!intMemList.isEmpty()) {
            Map<String, Integer> contCountMap = getSubsContactCount(intMemList, readOnly, persisterTxn);
            // exclude async delete mdns based on operationType
            memberList.addAll(getSubsDetails(operationType, intMemList, corpId, maxContactLimit, contCountMap, readOnly, persisterTxn));
        }
        if (!extMemList.isEmpty()) {
            // exclude async delete mdns
            if (KnOperationTypes.GET_SUBLIST_DETAILS.equals(operationType)) {
                extMemList = excludeAsyncDeletionInProgressMdns(extMemList, persisterTxn);
            }
            memberList.addAll(getExtSubListMemDetails(extMemList, corpId, readOnly, persisterTxn));
        }

        respDTO.setMemberList(memberList);
        knLogger.debug(methodName, " Exit -", respDTO);
        return respDTO;
    }

    public void deleteSublist(int sublistId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteSublist(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        deleteAllSublistMembers(sublistId, persisterTxn);
        Collection<Integer> sublistIdList = new ArrayList<Integer>();
        sublistIdList.add(sublistId);
        updateSublistsSubscribersContactCount(sublistIdList, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED, persisterTxn, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED);
        deleteCorpListDistReference(sublistId, persisterTxn);
        deleteCorpListDistGroupReference(sublistId, persisterTxn);
        deleteSublistInfo(sublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void deleteSublist(List<Integer> sublistId, int corpId, List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteSublist(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :::", sublistId, persisterTxn);
        deleteAllSublistMembers(sublistId, persisterTxn);
        Collection<Integer> sublistIdList = new ArrayList<Integer>();
        sublistIdList.addAll(sublistId);
        updateSublistsSubscribersContactCount(sublistIdList, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED, persisterTxn, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED);
        deleteCorpListDistReference(sublistId, mdnList, persisterTxn);
        deleteCorpListDistGroupReference(sublistId, persisterTxn);
        deleteSublistInfo(sublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    private void deleteSublistInfo(int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteSublistInfo(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        corpListDAO.deleteSublistInfo(sublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    private void deleteSublistInfo(List<Integer> sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteSublistInfo(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        corpListDAO.deleteSublistInfo(sublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void deleteCorpListDistReference(int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteCorpListDistReference(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpListDistInfoDAO corpListDistDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListDistInfoDAO(pttServerId);
        corpListDistDAO.deleteCorpListDistReference(sublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void deleteCorpListDistReference(List<Integer> sublistId, List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteCorpListDistReference(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpListDistInfoDAO corpListDistDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListDistInfoDAO(pttServerId);
        corpListDistDAO.deleteCorpListDistReference(sublistId, mdnList, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void deleteCorpListDistGroupReference(int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteCorpListDistGroupReference(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpGroupListRefDAO corpGroupListRefDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupListRefDAO(pttServerId);
        corpGroupListRefDAO.deleteCorpListDistGroupReference(sublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public KnCorpSublistDistributionRespDTO getDistributionList(int sublistId, int filterType,
                                                                int maxContactLimit, int maxGroupMemberLimit, int corpId,
                                                                String pttServerId, boolean readOnly,
                                                                KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getDistributionList(int, int, int, int, int, String,boolean, KnPersisterTxn";
        knLogger.debug(methodName, "Entry :");
        KnCorpSublistDistributionRespDTO respDTO = new KnCorpSublistDistributionRespDTO();
        switch (filterType) {
            case 1:
                Collection<KnCorpContactDTO> contactList =
                        getSublistSubscribersDistList(sublistId, maxContactLimit, corpId, pttServerId, readOnly, persisterTxn);
                respDTO.setContactList(contactList);
                break;
            case 2:
                Collection<KnCorpGroupInfoDTO> groupList =
                        getSublistGroupDistributionList(sublistId, maxGroupMemberLimit, pttServerId, readOnly, persisterTxn);
                respDTO.setGroupList(groupList);
                break;
            default:
                respDTO = getCompleteSublistDistributionList(sublistId, maxContactLimit, maxGroupMemberLimit, corpId, pttServerId, readOnly, persisterTxn);
        }
        knLogger.debug(methodName, "EXIT : Response returned- ", respDTO);
        return respDTO;
    }

    /**
     * This method returns the sulidt details pushed to the group
     *
     * @param groupId
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnCorpSublistDTO> getGroupsSublistList(int groupId, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getGroupsSublistList(int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        Connection conn;
        PreparedStatement pstmt = null;
        Statement stmtListDetails = null;
        Statement stmtListMemcount = null;
        ResultSet resultSet = null;
        ResultSet rsListDetails = null;
        ResultSet rsListCount = null;
        String query = null;
        String querySublistDetails;
        String querySublistMemCount;
        List<KnCorpSublistDTO> sublistList;
        Map<Integer, KnCorpSublistDTO> sublistMap;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_SUBLISTIDS);
            List<Integer> grpListIdList = new ArrayList<Integer>();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug(methodName, "QUERY : Executing - ", query);
            resultSet = pstmt.executeQuery();
            knLogger.debug(methodName, "QUERY : Completed");
            while (resultSet.next()) {
                grpListIdList.add(resultSet.getInt(1));
            }
            querySublistDetails = queryMapper.getQuery(GET_GROUPS_SUBLIST_LIST);
            querySublistDetails = replaceContactWithValue(querySublistDetails, SUBLISTID, formIntegerCommaSeperatedIdList(grpListIdList));
            stmtListDetails = conn.createStatement();
            knLogger.debug(methodName, "QUERY : Executing - ", querySublistDetails);
            rsListDetails = stmtListDetails.executeQuery(querySublistDetails);
            knLogger.debug(methodName, "QUERY : Completed", querySublistDetails);
            sublistMap = new HashMap<Integer, KnCorpSublistDTO>(grpListIdList.size());
            while (rsListDetails.next()) {
                KnCorpSublistDTO sublist = new KnCorpSublistDTO();
                int sublistId = rsListDetails.getInt(1);
                sublist.setSublistId(sublistId);
                //multilingual revert changes
                if (rsListDetails.getString(2) != null) {
                    try {
                        sublist.setSublistName(new String(rsListDetails.getString(2).getBytes("8859_1"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                sublist.setSublistType(1);
                sublist.setHierarchyId(rsListDetails.getString(3));
                sublistMap.put(sublistId, sublist);
            }
            querySublistMemCount = queryMapper.getQuery(GET_SUBLIST_MEMBER_COUNT);
            querySublistMemCount = replaceContactWithValue(querySublistMemCount, SUBLISTID, formIntegerCommaSeperatedIdList(grpListIdList));
            stmtListMemcount = conn.createStatement();
            knLogger.debug(methodName, "QUERY : Executing - ", querySublistMemCount);
            rsListCount = stmtListMemcount.executeQuery(querySublistMemCount);
            knLogger.debug(methodName, "QUERY : Completed", querySublistMemCount);
            while (rsListCount.next()) {
                KnCorpSublistDTO corpSublistDTO = sublistMap.get(rsListCount.getInt(1));
                if (null != corpSublistDTO) {
                    corpSublistDTO.setMemberCount(rsListCount.getInt(2));
                }
            }
            sublistList = new ArrayList<KnCorpSublistDTO>(sublistMap.values());
            knLogger.debug(methodName, "Sublist List size returned  - ", sublistList.size());
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to selectGroupsSublistIdInRemovedList ",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(resultSet);
            KnDbUtil.closeResultSet(rsListCount);
            KnDbUtil.closeResultSet(rsListDetails);
            KnDbUtil.closeStatement(stmtListDetails);
            KnDbUtil.closeStatement(stmtListMemcount);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return sublistList;
    }

    public Collection<Integer> getGroupsSublistIdInRemovedList(int groupId,
                                                               Collection<Integer> removedSublistIds,
                                                               String pttServerId,
                                                               KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupsSublistIdInRemovedList(int, Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpGroupListRefDAO corpGroupListRefDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupListRefDAO(pttServerId);
        Collection<Integer> sublistList =
                corpGroupListRefDAO.selectGroupsSublistIdInRemovedList(groupId, removedSublistIds, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return sublistList;
    }

    public KnMdnDetailsPersistDTO getGroupPrivateMemberListInfoFromMdnList(KnCorpMdnListPersistDTO corpMdnListPersistDto,
                                                                           int groupMemberlistId,
                                                                           KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupPrivateMemberListInfoFromMdnList(KnCorpMdnListPersistDTO, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListMemberDAO corpListMemDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        KnMdnDetailsPersistDTO mdnDetailsPersistDto = corpListMemDAO.selectGroupPrivateMemberListInfoFromMdnList
                (corpMdnListPersistDto, groupMemberlistId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return mdnDetailsPersistDto;
    }


    private Collection<KnCorpContactDTO> getSublistSubscribersDistList(int sublistId,
                                                                       int maxContactLimit, int corpId, String pttServerId, boolean readOnly,
                                                                       KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSublistSubscribersDistList(int,int,int, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMSubscriberInfoDAO subscInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        Map<String, Collection<KnCorpContactDTO>> contactMap =
                subscInfoDAO.getSublistSubscribersDistList(sublistId, maxContactLimit, corpId, readOnly, persisterTxn);
        Collection<KnCorpContactDTO> completeList = new ArrayList<KnCorpContactDTO>(contactMap.get(INTERNAL));
        Collection<KnCorpContactDTO> externalContacts = contactMap.get(EXTERNAL);
        Map<String, KnCorpContactDTO> externalSubscMap = convertToCorpSubscDTOMap(externalContacts);
        Collection<KnCorpContactDTO> extContactList = getExternalContactNames(externalSubscMap, corpId, readOnly, persisterTxn);
        completeList.addAll(extContactList);
        Collection<KnCorpSubscriberDTO> sublistContactsList = getSublistContactList(sublistId, readOnly, persisterTxn);
        for (KnCorpContactDTO contact : completeList) {
            if (sublistContactsList.contains(contact)) {
                contact.setDistributionType(1);
            }
        }
        knLogger.debug(methodName, "EXIT");
        return completeList;
    }

    private Map<String, KnCorpContactDTO> convertToCorpSubscDTOMap(Collection<KnCorpContactDTO> externalContacts) {
        Map<String, KnCorpContactDTO> subscMap = new HashMap<String, KnCorpContactDTO>();
        for (KnCorpContactDTO contactDTO : externalContacts) {
            subscMap.put(contactDTO.getMdn(), contactDTO);
        }
        return subscMap;
    }

    private Collection<KnCorpGroupInfoDTO> getSublistGroupDistributionList(int sublistId,
                                                                           int maxGroupMemberLimit, String pttServerId, boolean readOnly,
                                                                           KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSublistGroupDistributionList(int, int, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpGroupInfoDAO groupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        Collection<KnCorpGroupInfoDTO> groupList = groupInfoDAO.getSublistGroupDistributionList(sublistId, maxGroupMemberLimit, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return groupList;
    }

    private KnCorpSublistDistributionRespDTO getCompleteSublistDistributionList(int sublistId, int
            maxCountLimit, int maxGroupMemberLimit, int corpId, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {

        final String methodName = "getCompleteSublistDistributionList(int,int, int, int, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnCorpSublistDistributionRespDTO respDTO = new KnCorpSublistDistributionRespDTO();

        Collection<KnCorpContactDTO> contactList =
                getSublistSubscribersDistList(sublistId, maxCountLimit, corpId, pttServerId, readOnly, persisterTxn);
        respDTO.setContactList(contactList);
        Collection<KnCorpGroupInfoDTO> groupList =
                getSublistGroupDistributionList(sublistId, maxGroupMemberLimit, pttServerId, readOnly, persisterTxn);
        respDTO.setGroupList(groupList);
        knLogger.debug(methodName, "EXIT");
        return respDTO;
    }

    public void deleteAllSublistMembers(int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteAllSublistMembers(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpListMemberDAO corpListMemDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        corpListMemDAO.deleteAllSublistMembers(sublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void deleteAllSublistMembers(List<Integer> sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteAllSublistMembers(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpListMemberDAO corpListMemDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        corpListMemDAO.deleteAllSublistMembers(sublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    private Collection<KnCorpContactDTO> getExternalContactNames(Map<String, KnCorpContactDTO> extrenalContacts, int
            corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getExternalContactNames(Map<String, KnCorpContactDTO>, int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMExtPoCSubscriberDAO extSubscDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(pttServerId);
        Collection<KnCorpContactDTO> externalContact =
                extSubscDAO.getExternalContactNames(extrenalContacts, corpId, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return externalContact;
    }

    private Map<String, Collection<KnCorpContactDTO>> getContactDetails(Collection<KnCorpSubscriberDTO> membersList,
                                                                        int corpId,
                                                                        int maxContactLimit,
                                                                        KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getContactDetails(Collection<KnCorpSubscriberDTO>, int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMSubscriberInfoDAO subscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        Map<String, Collection<KnCorpContactDTO>> contactDetails =
                subscInfoDAO.getContactDetails(membersList, corpId, maxContactLimit, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return contactDetails;
    }


    public Map<String, KnCorpSubscriberDTO> getSubsribersCorporateDetails
            (Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getSubsribersCorporateDetails(mdnList, corpId, persisterTxn);
    }


    public Collection<KnCorpSublistDTO> getSubsMappedSublistList(KnIPCorpContactDTO contactDTO,
                                                                 int corpListId, KnPersisterTxn persisterTxn) throws KnDAOException {

        final String methodName = "getSubsMappedSublistList(KnIPCorpContactDTO, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        Collection<KnCorpSublistDTO> corpSubscList =
                corpListDAO.getSubsMappedSublistList(contactDTO, corpListId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return corpSubscList;
    }

    public Collection<KnCorpSublistDTO> getSubsMappedSublistList(Map<String, KnIPCorpContactDTO> contactDTOMap, int corpListId, KnPersisterTxn persisterTxn, int corpId) {
        final String methodName = "getSubsMappedSublistList(KnIPCorpContactDTO, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        Collection<KnCorpSublistDTO> corpSubscList =
                null;
        try {
            corpSubscList = corpListDAO.getSubsMappedSublistList(contactDTOMap, corpListId, persisterTxn, corpId);
        } catch (KnDAOException e) {
            knLogger.debug("Exception Happened::", e.getMessage());
        }
        knLogger.debug(methodName, "EXIT");
        return corpSubscList;
    }

    public Collection<KnCorpGroupInfoPersistDTO> selectGroupList(int corpId, int maxMemPerGroup, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn,String hierarchyId)
            throws KnDAOException {
        final String methodName = "selectGroupList(int, int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : corpId - ", corpId, ", maxMemPerGroup - ", maxMemPerGroup);
        String query = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = queryMapper.getQuery(GET_GROUP_LIST);
            if (hierarchyId != null && !hierarchyId.isEmpty()) {
                query=query.trim();
                if(query.endsWith(";")){
                    query = query.substring(0, query.length() - 1);
                }
                query += " AND (CGI.HIERARCHY_ID=? OR EXISTS (SELECT 1 FROM DG.GROUP_HIERARCHY_MAP GHM WHERE GHM.CORPGROUPID=CGI.CORPGROUPID AND GHM.ID_TYPE=4 AND GHM.CORPID=? AND GHM.ID_VALUE=?));";
            }
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            if (hierarchyId != null && !hierarchyId.isEmpty()) {
                pstmt.setString(2, hierarchyId);
                pstmt.setInt(3, corpId);
                pstmt.setInt(4, Integer.parseInt(hierarchyId));
            }
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ", query);
            Collection<KnCorpGroupInfoPersistDTO> groupList = new ArrayList<KnCorpGroupInfoPersistDTO>();
            while (rs.next()) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(rs.getInt(1));
                groupPersistDTO.setCorpId(corpId);
                //multilingual revert change
                if (rs.getString(2) != null) {
                    groupPersistDTO.setGroupDisplayName(new String(rs.getString(2).trim().getBytes("8859_1"), "UTF-8"));
                }
                int memCount = rs.getInt(3);
                groupPersistDTO.setMemberCount(memCount);
                if (memCount > maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(GREATER_THAN_LIMIT);
                } else if (memCount == maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(EQUAL_TO_LIMIT);
                } else if (memCount < MIN_MEMBER_LIMIT) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(LESS_THAN_MIN_LIMIT);
                } else if (memCount < maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(LESS_THAN_LIMIT);
                }
                groupPersistDTO.setGroupType(mappGroupTypeToApp(rs.getInt(4)));
                groupPersistDTO.setAvatar((Integer) rs.getObject(5));
                groupPersistDTO.setGroupCreatedBy(rs.getInt(6));
                if (rs.getInt(7) == 1) {
                    groupPersistDTO.setLargeGroup(Boolean.TRUE);
                } else if (rs.getInt(7) == 2) {
                    groupPersistDTO.setMcxGrpInd(MCX_GRP_INDICATOR);
                    //overriding the count
                    groupPersistDTO.setMemberCount(MCX_GRP_COUNT);
                }
                groupPersistDTO.setGroupProfileId(rs.getString(8));
                groupPersistDTO.setGrpShared((Integer) rs.getObject("GROUP_SHARED"));
                groupPersistDTO.setPocHome(rs.getString("POCHOME"));
                groupPersistDTO.setIsPreConfiguredGroup((Integer) rs.getObject("IS_PRECONFIG_GRP"));
                groupPersistDTO.setUgwInterop((Integer) rs.getObject("ugwInterop"));
                groupPersistDTO.setRecordingFs(rs.getString("RECORDING_FS"));
                if (null != rs.getObject("AUTHORIZED_LARGE_TG")) {
                    groupPersistDTO.setAuthorizedLargeTG(rs.getInt("AUTHORIZED_LARGE_TG"));
                } else {
                    groupPersistDTO.setAuthorizedLargeTG(KnConstants.DEFAULT_AUTHORIZED_LARGE_TG_VALUE);
                }
                if (null != rs.getObject("VIDEO_PERMISSION")) {
                    int videoPermValue = rs.getInt("VIDEO_PERMISSION");
                    groupPersistDTO.setVideoPermission(videoPermValue);
                    knLogger.debug(methodName, "DAO: Read VIDEO_PERMISSION from DB - groupId:", groupPersistDTO.getGroupId(), ", videoPermission:", videoPermValue);
                } else {
                    groupPersistDTO.setVideoPermission(KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                    knLogger.debug(methodName, "DAO: VIDEO_PERMISSION is NULL in DB, setting default - groupId:", groupPersistDTO.getGroupId(), ", default:", KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                }
                groupPersistDTO.setHierarchyId(rs.getString("HIERARCHY_ID"));

                groupList.add(groupPersistDTO);
            }
            knLogger.info(methodName, "corpId - ", corpId, ", maxMemPerGroup - ", maxMemPerGroup, "GroupList size - ", groupList.size());
            return groupList;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving GroupList - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve GroupList " + e,
                    pttServerId, KnDAOSourceTypes.GRPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Map<Integer, KnCorpGroupDTO> getGroupInfoList(int corpId, int groupId, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getGroupInfoList(int, int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : corpId - ", corpId, ", gropId - ", groupId);
        ResultSet rs = null;
        Connection conn = null;
        boolean ownedTxn = false;
        PreparedStatement pstmt = null;
        String query = "SELECT CGI.CORPGROUPID, CGI.GROUPDISPLAYNAME, GMC.MEMBERCOUNT, CGI.GROUPTYPE, CGI.AVATAR_ID, " +
                "CGI.GROUP_CREATED_BY, CGI.IS_LARGEGROUP, CGI.GRP_PROFILE_ID, CGI.GROUP_SHARED, CGI.POCHOME, " +
                "CGI.IS_PRECONFIG_GRP, CGI.UGWINTEROP, CGI.RECORDING_FS, CGI.VIDEO_PERMISSION " +
                "FROM DG.CORPGROUPINFO CGI " +
                "LEFT JOIN DG.CORPGROUPMEMBERCOUNT GMC ON CGI.CORPGROUPID = GMC.CORPGROUPID " +
                "WHERE CGI.CORPGROUPID = ?;";
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
            }
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, groupId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ", query);
            Map<Integer, KnCorpGroupDTO> groupList = new HashMap<>();
            while (rs.next()) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(rs.getInt(1));
                groupPersistDTO.setCorpId(corpId);
                //multilingual revert change
                if (rs.getString(2) != null) {
                    groupPersistDTO.setGroupDisplayName(new String(rs.getString(2).trim().getBytes("8859_1"), "UTF-8"));
                    knLogger.debug(methodName, "groupPersistDTO.getGroupDisplayName() - ", groupPersistDTO.getGroupDisplayName());
                }

                groupPersistDTO.setMemberCount(rs.getInt(3));
                groupPersistDTO.setGroupType(mappGroupTypeToApp(rs.getInt(4)));
                groupPersistDTO.setAvatar((Integer) rs.getObject(5));
                groupPersistDTO.setGroupCreatedBy(rs.getInt(6));
                if (rs.getInt(7) == 1) {
                    groupPersistDTO.setLargeGroup(Boolean.TRUE);
                } else if (rs.getInt(7) == 2) {
                    groupPersistDTO.setMcxGrpInd(MCX_GRP_INDICATOR);
                    //overriding the count
                    groupPersistDTO.setMemberCount(MCX_GRP_COUNT);
                }
                groupPersistDTO.setGroupProfileId(rs.getString(8));
                groupPersistDTO.setGrpShared((Integer) rs.getObject("GROUP_SHARED"));
                groupPersistDTO.setPocHome(rs.getString("POCHOME"));
                groupPersistDTO.setIsPreConfiguredGroup((Integer) rs.getObject("IS_PRECONFIG_GRP"));
                groupPersistDTO.setUgwInterop((Integer) rs.getObject("ugwInterop"));
                groupPersistDTO.setRecordingFs(rs.getString("RECORDING_FS"));
                if (null != rs.getObject("VIDEO_PERMISSION")) {
                    groupPersistDTO.setVideoPermission(rs.getInt("VIDEO_PERMISSION"));
                } else {
                    groupPersistDTO.setVideoPermission(KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                }

                groupList.put(groupId, groupPersistDTO);
            }
            knLogger.info(methodName, "corpId - ", corpId, ", gropIdList - ", groupId, "GroupList size - ", groupList.size());
            return groupList;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving GroupList - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve GroupList " + e,
                    pttServerId, KnDAOSourceTypes.GRPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
        }
    }

    public Collection<KnCorpGroupInfoPersistDTO> selectGroupListPaginated(int corpId, int maxMemPerGroup, int nextToken,
                                                                          int fetchSize, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn,String hierarchyId)
            throws KnDAOException {
        final String methodName = "selectGroupListPaginated(int, int, int, int, String, boolean, KnPersisterTxn)";
        knLogger.info(methodName, "Entry : corpId - ", corpId, ", maxMemPerGroup - ", maxMemPerGroup,
                ", nextToken - ", nextToken, ", fetchSize - ", fetchSize);
        String query = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = queryMapper.getQuery(GET_GROUP_LIST_PAGINATED);
            if (hierarchyId != null) {
                query = query.replace("ORDER BY", " AND (CGI.HIERARCHY_ID=? OR EXISTS (SELECT 1 FROM DG.GROUP_HIERARCHY_MAP GHM WHERE GHM.CORPGROUPID=CGI.CORPGROUPID AND GHM.ID_TYPE=4 AND GHM.CORPID=? AND GHM.ID_VALUE=?)) ORDER BY");
            }
            int startIndex = getStartIndex(fetchSize, nextToken);
            int endIndex = getEndIndex(fetchSize, nextToken);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, startIndex);
            pstmt.setInt(2, endIndex);
            pstmt.setInt(3, corpId);
            pstmt.setInt(4, corpId);
            if (hierarchyId != null) {
                pstmt.setString(5, hierarchyId);
                pstmt.setInt(6, corpId);
                pstmt.setInt(7, Integer.parseInt(hierarchyId));
            }
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ", query);
            Collection<KnCorpGroupInfoPersistDTO> groupList = new ArrayList<>();
            while (rs.next()) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(rs.getInt(1));
                groupPersistDTO.setCorpId(corpId);
                //multilingual revert change
                if (rs.getString(2) != null) {
                    groupPersistDTO.setGroupDisplayName(new String(rs.getString(2).trim().getBytes("8859_1"), "UTF-8"));
                }
                int memCount = rs.getInt(3);
                groupPersistDTO.setMemberCount(memCount);
                if (memCount > maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(GREATER_THAN_LIMIT);
                } else if (memCount == maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(EQUAL_TO_LIMIT);
                } else if (memCount < MIN_MEMBER_LIMIT) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(LESS_THAN_MIN_LIMIT);
                } else if (memCount < maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(LESS_THAN_LIMIT);
                }
                groupPersistDTO.setGroupType(mappGroupTypeToApp(rs.getInt(4)));
                groupPersistDTO.setAvatar((Integer) rs.getObject(5));
                groupPersistDTO.setGroupCreatedBy(rs.getInt(6));
                if (rs.getInt(7) == 1) {
                    groupPersistDTO.setLargeGroup(Boolean.TRUE);
                } else if (rs.getInt(7) == 2) {
                    groupPersistDTO.setMcxGrpInd(MCX_GRP_INDICATOR);
                    //overriding the count
                    groupPersistDTO.setMemberCount(MCX_GRP_COUNT);
                }
                groupPersistDTO.setGroupProfileId(rs.getString(8));
                groupPersistDTO.setGrpShared((Integer) rs.getObject("GROUP_SHARED"));
                groupPersistDTO.setPocHome(rs.getString("POCHOME"));
                groupPersistDTO.setIsPreConfiguredGroup((Integer) rs.getObject("IS_PRECONFIG_GRP"));
                groupPersistDTO.setUgwInterop((Integer) rs.getObject("UGWINTEROP"));
                groupPersistDTO.setRecordingFs(rs.getString("RECORDING_FS"));
                if (null != rs.getObject("AUTHORIZED_LARGE_TG")) {
                    groupPersistDTO.setAuthorizedLargeTG(rs.getInt("AUTHORIZED_LARGE_TG"));
                } else {
                    groupPersistDTO.setAuthorizedLargeTG(KnConstants.DEFAULT_AUTHORIZED_LARGE_TG_VALUE);
                }
                if (null != rs.getObject("VIDEO_PERMISSION")) {
                    int videoPermValue = rs.getInt("VIDEO_PERMISSION");
                    groupPersistDTO.setVideoPermission(videoPermValue);
                    knLogger.debug(methodName, "DAO: Read VIDEO_PERMISSION from DB - groupId:", groupPersistDTO.getGroupId(), ", videoPermission:", videoPermValue);
                } else {
                    groupPersistDTO.setVideoPermission(KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                    knLogger.debug(methodName, "DAO: VIDEO_PERMISSION is NULL in DB, setting default - groupId:", groupPersistDTO.getGroupId(), ", default:", KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                }
                groupPersistDTO.setHierarchyId(rs.getString("HIERARCHY_ID"));

                groupList.add(groupPersistDTO);
            }
            knLogger.info(methodName, "corpId - ", corpId, ", maxMemPerGroup - ", maxMemPerGroup, "GroupList size - ", groupList.size());
            return groupList;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occurred while retrieving GroupList - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve GroupList " + e,
                    pttServerId, KnDAOSourceTypes.GRPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Set<Integer> selectGroupListCount(int corpId,String hierarchyId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectGroupListCount(int, boolean, KnPersisterTxn)";
        knLogger.info(methodName, "ENTRY : corpId= ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Set<Integer> groupIds = new HashSet<>();
        try {
            query = queryMapper.getQuery(GET_GROUP_LIST_COUNT);
            if(hierarchyId!=null && !hierarchyId.isEmpty()){
                query=query.trim();
                if(query.endsWith(";")){
                    query = query.substring(0, query.length() - 1);
                }
                query += " AND (CGI.HIERARCHY_ID=? OR EXISTS (SELECT 1 FROM DG.GROUP_HIERARCHY_MAP GHM WHERE GHM.CORPGROUPID=CGI.CORPGROUPID AND GHM.ID_TYPE=4 AND GHM.CORPID=? AND GHM.ID_VALUE=?));";
            }
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            knLogger.debug(methodName, "Executing query:", query);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setInt(2, corpId);
            if(hierarchyId!=null && !hierarchyId.isEmpty()){
                pstmt.setString(3, hierarchyId);
                pstmt.setInt(4, corpId);
                pstmt.setInt(5, Integer.parseInt(hierarchyId));
            }
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully");
            while (rs.next()) {
                groupIds.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            knLogger.error(methodName, "Unexpected SQLException occurred while retrieving GroupList count - ", e);
            throw KnDbUtil.processException(e, "Failed to fetch Group list count",
                    pttServerId, KnDAOSourceTypes.GRPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.info(methodName, "EXIT : group ids size", groupIds.size());
        }
        return groupIds;
    }

    public Collection<KnCorpGroupInfoPersistDTO> selectSubsGroupList(String subsMdn, int corpId, int
            maxMemPerGroup, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubsGroupList(String, int, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : subsMdn - ", KnGDPRTemplate.mdn(subsMdn), ", corpId - ", corpId, ", maxMemPerGroup - ", maxMemPerGroup);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Connection conn = null;
        boolean ownedTxn = false;
        Collection<KnCorpGroupInfoPersistDTO> groupList = new ArrayList<KnCorpGroupInfoPersistDTO>();
        try {
            if (persisterTxn != null) {
                knLogger.debug(methodName, " Data Store before - ", KnDBConst.DataStores.XDM_SHARED_DATA);
                conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
                knLogger.debug(methodName, "if block");
            } else {
                conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
                ownedTxn = true;
                knLogger.debug(methodName, "else block");
                knLogger.debug(methodName, "conn", conn);
            }
            query = queryMapper.getQuery(GET_SUBSC_GROUP_LIST);
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            //Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            pstmt.setString(2, subsMdn);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(rs.getInt(1));
                //multilingual revert change
                if (rs.getString(2) != null) {
                    groupPersistDTO.setGroupDisplayName(new String(rs.getString(2).trim().getBytes("8859_1"), "UTF-8"));
                }
                int memCount = rs.getInt(3);
                if (memCount > maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(GREATER_THAN_LIMIT);
                } else if (memCount == maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(EQUAL_TO_LIMIT);
                } else if (memCount < MIN_MEMBER_LIMIT) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(LESS_THAN_MIN_LIMIT);
                } else if (memCount < maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(LESS_THAN_LIMIT);
                }
                /*int broadcaster = rs.getInt(7);
                if (broadcaster == KnPersisterConstants.IS_BROADCASTER) {
                    List<KnCorpGroupMemberDTO> mdnList = new ArrayList<KnCorpGroupMemberDTO>();
                    KnCorpGroupMemberDTO memberDTO = new KnCorpGroupMemberDTO();
                    memberDTO.setMdn(subsMdn);
                    memberDTO.setBroadcaster(broadcaster);
                    mdnList.add(memberDTO);
                    groupPersistDTO.setGroupSupervisor(mdnList);
                }*/
                groupPersistDTO.setGroupMemberCount(memCount);
                groupPersistDTO.setETag(rs.getInt(4));
                groupPersistDTO.setGroupType(mappGroupTypeToApp(rs.getInt(5)));
                groupPersistDTO.setAvatar((Integer) rs.getObject(6));
                if (rs.getInt(7) == 1) groupPersistDTO.setBroadcasterCount(1);
                groupPersistDTO.setGroupCreatedBy(rs.getInt(8));
                if (rs.getInt(9) == 1) {
                    groupPersistDTO.setLargeGroup(Boolean.TRUE);
                } else if (rs.getInt(9) == 2) {
                    groupPersistDTO.setMcxGrpInd(MCX_GRP_INDICATOR);
                    //overriding group member count to 0
                    groupPersistDTO.setGroupMemberCount(MCX_GRP_COUNT);
                }
                groupPersistDTO.setOSMListId(String.valueOf(rs.getInt(10)));
                groupPersistDTO.setGroupProfileId(rs.getString(11));
                groupPersistDTO.setGrpShared((Integer)rs.getObject("GROUP_SHARED"));
                if (null != rs.getObject("VIDEO_PERMISSION")) {
                    groupPersistDTO.setVideoPermission(rs.getInt("VIDEO_PERMISSION"));
                } else {
                    groupPersistDTO.setVideoPermission(KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                }
                groupList.add(groupPersistDTO);
            }
            knLogger.debug(methodName, "Group List fetched sucecssfully.Size of List  - ", groupList.size());
            return groupList;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving Subs GroupList - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to retrieve Subs GroupList " + e,
                    pttServerId, KnDAOSourceTypes.SUBSGRPLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
            if (ownedTxn) {
                KnDbUtil.closeConnection(conn);
            }
            knLogger.debug(methodName, "inputSubsMdn - ", KnGDPRTemplate.mdn(subsMdn), ", corpId - ", corpId, ", maxMemPerGroup - ", maxMemPerGroup, "EXIT :Group List fetched sucecssfully.Size of List  - ", groupList.size());
        }
    }

    public Collection<KnCorpGroupInfoPersistDTO> getSubsGroupListForXcapOnMdn(String subsMdn, int maxMemPerGroup, boolean readOnly,
                                                                              KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubsGroupListForXcapOnMdn(String, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : subsMdn - ", KnGDPRTemplate.mdn(subsMdn), "maxMemPerGroup - ", maxMemPerGroup);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpGroupInfoPersistDTO> groupList = new ArrayList<KnCorpGroupInfoPersistDTO>();
        try {
            query = queryMapper.getQuery(GET_SUBSC_GROUP_LIST_ON_MDN);
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, subsMdn);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(rs.getInt(1));
                //multilingual revert change
                if (rs.getString("GROUPDISPLAYNAME") != null) {
                    groupPersistDTO.setGroupDisplayName(new String(rs.getString(2).trim().getBytes("8859_1"), "UTF-8"));
                }
                int memCount = rs.getInt(3);
                if (memCount > maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(GREATER_THAN_LIMIT);
                } else if (memCount == maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(EQUAL_TO_LIMIT);
                } else if (memCount < MIN_MEMBER_LIMIT) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(LESS_THAN_MIN_LIMIT);
                } else if (memCount < maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(LESS_THAN_LIMIT);
                }
                groupPersistDTO.setGroupMemberCount(memCount);
                groupPersistDTO.setETag(rs.getInt(4));
                groupPersistDTO.setGroupType(mappGroupTypeToApp(rs.getInt(5)));
                groupPersistDTO.setAvatar((Integer) rs.getObject(6));
                if (rs.getInt(7) == 1) groupPersistDTO.setBroadcasterCount(1);
                groupPersistDTO.setGroupCreatedBy(rs.getInt(8));
                if (rs.getInt(9) == 1) {
                    groupPersistDTO.setLargeGroup(Boolean.TRUE);
                } else if (rs.getInt(9) == 2) {
                    groupPersistDTO.setMcxGrpInd(MCX_GRP_INDICATOR);
                    //overriding group member count to 0
                    groupPersistDTO.setGroupMemberCount(MCX_GRP_COUNT);
                }
                groupPersistDTO.setOSMListId(String.valueOf(rs.getInt(10)));
                groupPersistDTO.setGroupProfileId(rs.getString(11));
                groupPersistDTO.setGrpShared((Integer) rs.getObject("GROUP_SHARED"));
                groupPersistDTO.setGroupCorpId(rs.getInt(13));
                if (null != rs.getObject("VIDEO_PERMISSION")) {
                    groupPersistDTO.setVideoPermission(rs.getInt("VIDEO_PERMISSION"));
                } else {
                    groupPersistDTO.setVideoPermission(KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                }
                groupList.add(groupPersistDTO);
            }
            knLogger.debug(methodName, "Group List fetched sucecssfully.Size of List  - ", groupList.size());
            return groupList;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving Subs GroupList - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to retrieve Subs GroupList " + e,
                    pttServerId, KnDAOSourceTypes.SUBSGRPLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public Collection<KnCorpGroupInfoPersistDTO> selectSubsLocGroupList(String subsMdn, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectSubsLocGroupList(String, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : subsMdn - ", KnGDPRTemplate.mdn(subsMdn), ", corpId - ", corpId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Collection<KnCorpGroupInfoPersistDTO> groupList = new ArrayList<KnCorpGroupInfoPersistDTO>();
        //Map<Integer, Collection<KnCorpGroupInfoPersistDTO>> groupListMap = new HashMap<>();
        Map<Integer, Collection<KnCorpGroupMemberDTO>> memberListMap = new HashMap<>();
        Collection<Integer> groupIds = new ArrayList<>();
        try {
            query = queryMapper.getQuery(GET_SUBSC_GROUP_LIST_LOCWATCHER);
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            // pstmt.setInt(2, KnPersisterConstants.IS_LOCWATCHER);
            pstmt.setString(2, subsMdn);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(rs.getInt(1));
                groupIds.add(rs.getInt(1));
                ///multilingual revert change
                if (rs.getString(2) != null) {
                    groupPersistDTO.setGroupDisplayName(new String(rs.getString(2).trim().getBytes("8859_1"), "UTF-8"));
                }
                int memCount = rs.getInt(3);
                groupPersistDTO.setGroupMemberCount(memCount);
                groupPersistDTO.setETag(rs.getInt(4));
                groupPersistDTO.setGroupType(mappGroupTypeToApp(rs.getInt(5)));
                if (rs.getInt(6) == 2) {
                    groupPersistDTO.setMcxGrpInd(MCX_GRP_INDICATOR);
                    //overriding the count to 0 since it is VLG group
                    groupPersistDTO.setGroupMemberCount(MCX_GRP_COUNT);
                }
                groupList.add(groupPersistDTO);
                //groupListMap.put(rs.getInt(1), groupList);
            }
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            rs = null; pstmt = null;

            if (!groupIds.isEmpty()) {
                query = queryMapper.getQuery(GROUP_CORP_MEMBER_LIST);
                query = replaceContactWithValue(query, GROUPIDS, formIntegerCommaSeperatedIdList(groupIds));
                knLogger.debug(methodName, "Executing query - ", query);
                pstmt = conn.prepareStatement(query);
                knLogger.debug(methodName, "Executing query - ", query);
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully");
                Collection<KnCorpGroupMemberDTO> memListDTO = null;
                while (rs.next()) {
                    if (IS_LOCWATCHER == rs.getInt(4)) {
                        if (memberListMap.get(rs.getInt(1)) != null) {
                            memListDTO = memberListMap.get(rs.getInt(1));
                        } else {
                            memListDTO = new ArrayList<>();
                        }
                        KnCorpGroupMemberDTO memDTO = new KnCorpGroupMemberDTO();
                        memDTO.setMdn(rs.getString(2).trim());
                        memDTO.setSupervisory(rs.getInt(3));
                        memDTO.setLocWatcher(rs.getInt(4));
                        memListDTO.add(memDTO);
                        memberListMap.put(rs.getInt(1), memListDTO);
                    }
                }
            }
            for (KnCorpGroupInfoPersistDTO grpListDto : groupList) {
                if (memberListMap.get(grpListDto.getGroupId()) != null) {
                    grpListDto.setGroupSupervisor(memberListMap.get(grpListDto.getGroupId()));
                }
            }

            knLogger.debug(methodName, "Group List fetched sucecssfully.Size of List  - ", groupList.size());
            return groupList;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while retrieving Subs GroupList - ",
                    e);
            throw KnDbUtil.processException(e, "Failed to retrieve Subs GroupList " + e,
                    pttServerId, KnDAOSourceTypes.SUBSGRPLIST, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }

    public KnCorpGroupInfoPersistDTO selectGroupNameInfo(int corpId, String groupName, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "selectGroupNameInfo(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        //get group count
        KnCorpGroupInfoPersistDTO groupPersistDTO = groupInfoDAO.selectGroupNameInfo(corpId, groupName, persisterTxn);
        return groupPersistDTO;
    }

    public Map<String, Integer> selectSubscriberGroupCounts(Collection<String> mdnList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectSubscriberGroupCounts(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");

        KnCorpGroupDistInfoDAO groupDistInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupDistInfoDAO(pttServerId);
        //get group count
        Map<String, Integer> subsGroupCountMap = groupDistInfoDAO.selectSubscriberGroupCounts(mdnList, persisterTxn);
        return subsGroupCountMap;
    }


    public void createGroupInfo(KnCorpGroupInfoPersistDTO groupInfoPersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createGroupInfo(KnCorpGroupInfoPersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName);
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        //insert group info
        corpGroupInfoDAO.insert(groupInfoPersistDTO, persisterTxn);
    }

    public void insertGroupListRefInfo(int groupId, Collection<Integer> sublistIds, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "insertGroupListRefInfo(int, Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnCorpGroupListRefDAO groupListRefDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupListRefDAO(pttServerId);

        groupListRefDAO.insert(groupId, sublistIds, persisterTxn);
    }

    /**
     * returns the group information from dg.corpgroupinfo table.
     *
     * @param groupId
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnCorpGroupInfoPersistDTO selectGroupInfo(int groupId, int corpId, int clintIntf, KnPersisterTxn persisterTxn, Boolean hiearchyCall)
            throws KnDAOException, KnXDMServerException {
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        return corpGroupInfoDAO.selectGroupInfo(groupId, corpId, clintIntf, persisterTxn, hiearchyCall);
    }


    public KnCorpGroupInfoPersistDTO selectGroupInfoByGroupId(int groupId, int clintIntf, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        return corpGroupInfoDAO.selectGroupInfoByGroupId(groupId, clintIntf, readOnly, persisterTxn);
    }

    public void getExternalGroupMembersContactNames(Map<String, KnCorpGroupMemPersistDTO> externalContacts,
                                                    int corpId, KnPersisterTxn persisterTxcn) throws KnDAOException {

        String methodName = "getExternalGroupMembersContactNames(Map<String, KnCorpGroupMemPersistDTO>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMExtPoCSubscriberDAO extPoCSubsDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(pttServerId);
        extPoCSubsDAO.selectExternalGroupMembersContactNames(externalContacts, corpId, persisterTxcn);
        knLogger.debug(methodName, "EXIT :");
    }

    public int getGroupCountByName(String groupName, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupCountByName(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : groupName - ", groupName, ", corpId - ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        int count = 0;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_COUNT_BY_NAME);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            //multilingual revert changes
            if (groupName != null) {
                groupName = new String(groupName.getBytes("UTF-8"), "8859_1");
            }
            pstmt.setString(1, groupName);
            pstmt.setInt(2, corpId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while getGroupCountByName - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve group member list " + e,
                    pttServerId, KnDAOSourceTypes.GRPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
            knLogger.debug(methodName, "groupName - ", groupName, ", corpId - ", corpId, "EXIT : No of groups with the given name -", count);
        }
    }


    public void deleteGroupSublistRef(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteGroupSublistRef(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");

        KnXDMCorpGroupListRefDAO listRef =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupListRefDAO(pttServerId);
        listRef.deleteGroupSublistRef(groupId, persisterTxn);
        knLogger.debug(methodName, "EXIT :");
    }


    public void deleteGroupPrivateList(int corpListId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteGroupPrivateList(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        deleteSublistInfo(corpListId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void deleteGroup(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteGroup(int,KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpGroupInfoDAO groupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        groupInfoDAO.deleteGroup(groupId, persisterTxn);
        knLogger.debug(methodName, "EXIT :");
    }

    public void deleteGroupDistribution(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteGroupDistribution(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnCorpGroupDistInfoDAO groupDistInfo =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpGroupDistInfoDAO(pttServerId);
        groupDistInfo.deleteGroupDistribution(groupId, persisterTxn);
        knLogger.debug(methodName, "EXIT :");
    }

    public void updateGroupMemberListId(int groupId, int corpSublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateGroupMemberListId(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnCorpGroupInfoDAO corpGrpInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpGroupInfoDAO(pttServerId);
        corpGrpInfoDAO.updateGroupMemberListId(groupId, corpSublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT :");
    }

    public int getCorpGroupCount(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpGroupCount(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        int count = 0;
        KnCorpGroupInfoDAO corpGrpInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpGroupInfoDAO(pttServerId);
        count = corpGrpInfoDAO.getCorpGroupCount(corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT :");
        return count;
    }

    public void addToCorpGroupDistInfo(Collection<KnCorpSubscriberDTO> actualMdnToBeAddedToGroup, int groupId,
                                       KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "addToCorpGroupDistInfo(Collection<KnCorpSubscriberDTO>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnCorpGroupDistInfoDAO corpGroupDistDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpGroupDistInfoDAO(pttServerId);
        corpGroupDistDAO.insertToCorpGroupDistInfo(actualMdnToBeAddedToGroup, groupId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
    }


    public void pushSublistListToSubscriberList(KnIPCorpSublistSubscDistDTO distDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "pushSublistListToSubscriberList(KnIPCorpSublistSubscDistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpListDistInfoDAO corpListDistDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListDistInfoDAO(pttServerId);
        corpListDistDAO.pushSublistListToSubscriberList(distDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
    }


    public Collection<KnCorpSubscriberDTO> getExternalPoCSubscriberDetails(Collection<KnCorpSubscriberDTO> contactsList,
                                                                           int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getExternalPoCSubscriberDetails(Collection<KnCorpSubscriberDTO>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMExtPoCSubscriberDAO corpListDistDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(pttServerId);
        Collection<KnCorpSubscriberDTO> externalContactsDetails =
                corpListDistDAO.getExternalPoCSubscriberDetails(contactsList, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return externalContactsDetails;
    }

    public int getSubscribersDocumentEtag(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscribersDocumentEtag(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpResourceListIndexDocDAO corpListDistDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpResourceListIndexDocDAO(pttServerId);
        int etag = corpListDistDAO.selectSubscribersDocumentEtag(mdn, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return etag;
    }

    public Map<String, Integer> getSubscribersDocumentEtag(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscribersDocumentEtag(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpResourceListIndexDocDAO corpListDistDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpResourceListIndexDocDAO(pttServerId);
        return corpListDistDAO.selectSubscribersDocumentEtag(mdnList, persisterTxn);
    }

    public int getSubscribersCount(String subscriberMdn, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscribersCount(String, int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMSubscriberInfoDAO subscDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        int count = subscDAO.selectSubscribersCount(subscriberMdn, corpId, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return count;
    }

    public int selectCorpId(String extCorpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "selectCorpId(String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpInfoDAO corpInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpInfoDAO(pttServerId);
        int corpId = corpInfoDAO.selectCorpId(extCorpId, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return corpId;
    }

    public long getCorporateEtag(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorporateEtag(int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpInfoDAO corpInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpInfoDAO(pttServerId);
        long count = corpInfoDAO.getCorporateEtag(corpId, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return count;
    }

    /**
     * This method queries the DG.XDM_CORPRESOURCELISTINDEXDOC table for subscriber etag.
     *
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public int getSubscriberResourceListEtag(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        return subscInfoDAO.getSubscriberResourceListEtag(mdn, readOnly, persisterTxn);
    }

    public Map<Integer, Integer> fetchAndUpdateSublistEtag(Collection<Integer> sublistIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListInfoDAO subscInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        Map<Integer, Integer> etagMap = subscInfoDAO.fetchAndUpdateSublistEtag(sublistIdList, persisterTxn);
        return etagMap;
    }

    public long updateCorporateEtag(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateCorporateEtag(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnXDMCorpInfoDAO corpInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpInfoDAO(pttServerId);
        long etag = corpInfoDAO.updateCorporateEtag(corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return etag;
    }

    public Map<String, Integer> getFinalMemberContactCount(Collection<Integer> finalSublistListInDB, Collection<String> finalMdnInPrivateList,
                                                           String subscriberMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getFinalMemberContactCount(Collection<Integer>, Collection<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMSubscriberInfoDAO subscriberInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        Map<String, Integer> countInfo = subscriberInfoDAO.getFinalMemberContactCount(finalSublistListInDB, finalMdnInPrivateList,
                subscriberMdn, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return countInfo;
    }

    public void updateSubscribersContactCount(Collection<String> mdnList, int maxSubscContactCount, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateSubscribersContactCount(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnXDMCorpContactCountDAO countactCountDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactCountDAO(pttServerId);
        countactCountDAO.updateSubscribersContactCount(mdnList, maxSubscContactCount, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
    }


    public void updateSublistsSubscribersContactCount(Collection<Integer> sublistIdsList, int maxContactLimit, int
            maxGrpContactLimit, int maxDispGrpContactLimit, KnPersisterTxn persisterTxn, int maxBGMemCount) throws KnDAOException {
        final String methodName = "updateSublistsSubscribersContactCount(Collection<Integer>, int, int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpContactCountDAO countactCountDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactCountDAO(pttServerId);
        countactCountDAO.updateSublistsSubscribersContactCount(sublistIdsList,
                maxContactLimit, maxGrpContactLimit, maxDispGrpContactLimit, persisterTxn, maxBGMemCount);
        knLogger.debug(methodName, "EXIT Point.");
    }

    private int getSize(Collection dataList) {
        if (dataList != null) {
            return dataList.size();
        } else {
            return 0;
        }
    }

    private int getMapSize(Map dataMap) {
        if (dataMap != null) {
            return dataMap.size();
        } else {
            return 0;
        }
    }

    public Map<String, KnOPDirChgDTO> updateSubcribersResourceListIndexDoc(Collection<String> mdnList,
                                                                           Map<String, KnOPDirChgDTO> etagMaps, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateSubcribersResourceListIndexDoc(Collection<String>, Map<String, KnOPDirChgDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdnList size - ", getSize(mdnList), ", etagMaps size - ", getMapSize(etagMaps));
        KnXDMCorpResourceListIndexDocDAO resourceDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpResourceListIndexDocDAO(pttServerId);
        Map<String, KnOPDocChgDTO> currResourcEtag = resourceDAO.updateEtag(mdnList, persisterTxn);
        // Map<String, KnOPDirChgDTO> etag = new HashMap<String, KnOPDirChgDTO>();
        if (etagMaps == null) {
            etagMaps = new HashMap<String, KnOPDirChgDTO>();
        }
        for (Map.Entry<String, KnOPDocChgDTO> entry : currResourcEtag.entrySet()) {
            String mdn = entry.getKey();
            KnOPDirChgDTO directory = etagMaps.get(mdn);
            if (directory == null) {
                directory = new KnOPDirChgDTO();
            }
            Collection<KnOPDocChgDTO> documents = directory.getDocChgDTO();
            if (documents == null) {
                documents = new ArrayList<KnOPDocChgDTO>();
            }
            documents.add(entry.getValue());
            directory.setDocChgDTO(documents);
            etagMaps.put(mdn, directory);
        }

        knLogger.debug(methodName, "mdnList size - ", getSize(mdnList), ", etagMaps size - ", getMapSize(etagMaps), "EXIT Point.");
        return etagMaps;
    }

    public void updateEtagForSubMdn(Map<String, Integer> mdnEtagMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateEtagForSubMdn(Map<String,Intger>, KnPersisterTxn)";
        KnXDMCorpResourceListIndexDocDAO resourceDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpResourceListIndexDocDAO(pttServerId);
        resourceDAO.updateEtagForSubMdn(mdnEtagMap, persisterTxn);
    }

    public Map<String, KnOPDirChgDTO> updateDistinctSubcribersDirectory(Collection<String> mdnList,
                                                                        Collection<Integer> groupIdLst,
                                                                        Map<String, KnOPDirChgDTO> etags, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateDistinctSubcribersDirectory(Collection<String>, Collection<Integer>, Map<String, KnOPDirChgDTO> etags, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdnList size- ", getSize(mdnList), ", groupIdLst size - ", getSize(groupIdLst), "etags - ", KnGDPRTemplate.mapKeyMdn(etags));
        KnXDMSubscriberInfoDAO subscrInfoDao =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        Collection<String> membersMdn = subscrInfoDao.getDistinctMembers(mdnList, groupIdLst, persisterTxn);
        if (etags != null) {
            Set<String> etagMdns = etags.keySet();
            List<String> externalMdns = new ArrayList<>(etagMdns);
            externalMdns.removeAll(membersMdn);
            knLogger.debug(methodName, " externalMdns :", externalMdns);
            if (!externalMdns.isEmpty()) {
                membersMdn.addAll(externalMdns);
            }
        }
        Map<String, KnOPDirChgDTO> etagMap = updateSubscribersDirectroy(membersMdn, etags, persisterTxn);
        Map<String, KnCorpSubscriberDTO> subscrInfoMap = new HashMap<String, KnCorpSubscriberDTO>();
        if (membersMdn != null) {
            subscrInfoMap = subscrInfoDao.getSubsribersCorporateDetails(membersMdn, persisterTxn);
        }
        for (Map.Entry<String, KnOPDirChgDTO> entry : etags.entrySet()) {
            String mdn = entry.getKey();
            KnOPDirChgDTO directory = etagMap.get(mdn);
            KnOPDirChgDTO dir = entry.getValue();
            if (!isObjectNull(directory)) {
                if (!isObjectNull(dir)) {
                    Collection<KnOPDocChgDTO> intialDoc = dir.getDocChgDTO();
                    if (intialDoc == null) {
                        intialDoc = new ArrayList<KnOPDocChgDTO>();
                    }
                    KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
                    if (subsc != null) {
                        directory.setPocHome(subsc.getPocHome());
                        directory.setPresenceHome(subsc.getPresenceHome());
                        directory.setNotfnCapability(subsc.isNotfnCapabiliy());
                        directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                        directory.setClientType(subsc.getClientType());
                        //sending Notification to Profile MDN..
                        boolean subsUpmBit = KnGeneralUtil.getFeatureBitValue(subsc.getSubsActiveFS2(), USER_PROFILE_MGMT_BIT);
                        knLogger.debug(methodName, "subsUpmBit :", subsUpmBit);
                        if (subsUpmBit) {
                            //0 - Notify for Base+Profile Mdn,1 - Notify Only for Profile Mdn
                            directory.setNtfyOnAnyMDN(0);
                        }
                    }
                    directory.setDocChgDTO(intialDoc);
                }
            } else {
                if (!isObjectNull(dir)) {
                    KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
                    if (subsc != null) {
                        dir.setPocHome(subsc.getPocHome());
                        dir.setPresenceHome(subsc.getPresenceHome());
                        dir.setNotfnCapability(subsc.isNotfnCapabiliy());
                        directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                        directory.setClientType(subsc.getClientType());
                    }
                    etagMap.put(mdn, dir);
                }
            }
        }
        knLogger.debug(methodName, "mdnList size- ", getSize(mdnList), ", groupIdLst size - ", getSize(groupIdLst), "etags - ", KnGDPRTemplate.mapKeyMdn(etags), "EXIT Point.", KnGDPRTemplate.mapKeyMdn(etagMap));
        return etagMap;
    }

    public Map<String, KnOPDirChgDTO> updateDistinctSubcribersDirectory(Collection<String> mdnList,
                                                                        Collection<Integer> groupIdLst,
                                                                        Map<String, KnOPDirChgDTO> etags
            , boolean upmDocNotify, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateDistinctSubcribersDirectory(Collection<String>, Collection<Integer>, Map<String, KnOPDirChgDTO> etags, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdnList size- ", getSize(mdnList),
                ", groupIdLst size - ", getSize(groupIdLst), "etags - ", KnGDPRTemplate.mapKeyMdn(etags), " upmDocNotify -", upmDocNotify);
        KnXDMSubscriberInfoDAO subscrInfoDao =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        Collection<String> membersMdn = subscrInfoDao.getDistinctMembers(mdnList, groupIdLst, persisterTxn);
        if (etags != null) {
            Set<String> etagMdns = etags.keySet();
            List<String> externalMdns = new ArrayList<>(etagMdns);
            externalMdns.removeAll(membersMdn);
            knLogger.debug(methodName, " externalMdns :", externalMdns);
            if (!externalMdns.isEmpty()) {
                membersMdn.addAll(externalMdns);
            }
        }
        Map<String, KnOPDirChgDTO> etagMap = updateSubscribersDirectroy(membersMdn, etags, persisterTxn);
        Map<String, KnCorpSubscriberDTO> subscrInfoMap = new HashMap<String, KnCorpSubscriberDTO>();
        if (membersMdn != null) {
            subscrInfoMap = subscrInfoDao.getSubsribersCorporateDetails(membersMdn, persisterTxn);
        }
        for (Map.Entry<String, KnOPDirChgDTO> entry : etags.entrySet()) {
            String mdn = entry.getKey();
            KnOPDirChgDTO directory = etagMap.get(mdn);
            KnOPDirChgDTO dir = entry.getValue();
            if (!isObjectNull(directory)) {
                if (!isObjectNull(dir)) {
                    Collection<KnOPDocChgDTO> intialDoc = dir.getDocChgDTO();
                    if (intialDoc == null) {
                        intialDoc = new ArrayList<KnOPDocChgDTO>();
                    }
                    KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
                    if (subsc != null) {
                        directory.setPocHome(subsc.getPocHome());
                        directory.setPresenceHome(subsc.getPresenceHome());
                        boolean notfnCapability = !upmDocNotify && subsc.isNotfnCapabiliy();
                        directory.setNotfnCapability(notfnCapability);
                        directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                        directory.setClientType(subsc.getClientType());
                        if (notfnCapability) {
                            boolean enabled76 = KnGeneralUtil.getFeatureBitValue(subsc.getSubsActiveFS2(), 76);
                            if (enabled76) directory.setNtfyOnAnyMDN(0);
                        }
                    }
                    directory.setDocChgDTO(intialDoc);
                }
            } else {
                if (!isObjectNull(dir)) {
                    KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
                    if (subsc != null) {
                        dir.setPocHome(subsc.getPocHome());
                        dir.setPresenceHome(subsc.getPresenceHome());
                        boolean notfnCapability = !upmDocNotify && subsc.isNotfnCapabiliy();
                        dir.setNotfnCapability(notfnCapability);
                        directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                        directory.setClientType(subsc.getClientType());
                        if (notfnCapability) {
                            boolean enabled76 = KnGeneralUtil.getFeatureBitValue(subsc.getSubsActiveFS2(), 76);
                            if (enabled76) dir.setNtfyOnAnyMDN(0);
                        }
                    }
                    etagMap.put(mdn, dir);
                }
            }
        }
        knLogger.debug(methodName, "mdnList size- ", getSize(mdnList), ", groupIdLst size - ", getSize(groupIdLst), "etags - ", KnGDPRTemplate.mapKeyMdn(etags), "EXIT Point.", KnGDPRTemplate.mapKeyMdn(etagMap));
        return etagMap;
    }

    public Map<String, KnOPDirChgDTO> updateDistinctSubcribersETags(Collection<String> mdnList,
                                                                    Collection<String> selfMdnList,
                                                                    Collection<Integer> groupIdLst,
                                                                    Map<String, KnOPDirChgDTO> etags,
                                                                    boolean updateResourceDaoETag,
                                                                    KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateDistinctSubcribersETags(Collection<String>, Collection<String> Collection<Integer>, " +
                "Map<String, KnOPDirChgDTO> etags, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdnList size- ", mdnList.size(), ", groupIdLst size - ", groupIdLst.size(),
                "etags - ", KnGDPRTemplate.mapKeyMdn(etags));

        KnXDMSubscriberInfoDAO subscrInfoDao =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        Collection<String> membersMdn = subscrInfoDao.getDistinctMembers(mdnList, groupIdLst, persisterTxn);
        if (etags != null) {
            Set<String> etagMdns = etags.keySet();
            List<String> externalMdns = new ArrayList<>(etagMdns);
            externalMdns.removeAll(membersMdn);
            if (!externalMdns.isEmpty()) {
                membersMdn.addAll(externalMdns);
            }
        }

        if (updateResourceDaoETag) {
            KnXDMCorpResourceListIndexDocDAO resourceDAO =
                    KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpResourceListIndexDocDAO(pttServerId);
            Map<String, KnOPDocChgDTO> currResourcEtag = resourceDAO.updateEtag(selfMdnList, persisterTxn);
        }
        KnXDMDirectoryDAO directoryDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMDirectoryDAO(pttServerId);
        Map<String, KnOPDirChgDTO> etagMap = directoryDAO.updateEtag(membersMdn, persisterTxn, etags);

        Map<String, KnCorpSubscriberDTO> subscrInfoMap = subscrInfoDao.getSubsribersCorporateDetails(membersMdn, persisterTxn);

        return etagMap;
    }

    public Map<String, KnOPDirChgDTO> updateDistinctSubcribersDirectoryClone(Collection<String> mdnList,
                                                                             Collection<Integer> groupIdLst,
                                                                             Map<String, KnOPDirChgDTO> etags
            , boolean upmDocNotify, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateDistinctSubcribersDirectoryClone(Collection<String>, Collection<Integer>, Map<String, KnOPDirChgDTO> etags, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdnList size- ", getSize(mdnList),
                ", groupIdLst size - ", getSize(groupIdLst), "etags - ", KnGDPRTemplate.mapKeyMdn(etags), " upmDocNotify -", upmDocNotify);
        KnXDMSubscriberInfoDAO subscrInfoDao =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        Collection<String> membersMdn = subscrInfoDao.getDistinctMembers(mdnList, groupIdLst, persisterTxn);
        if (etags != null) {
            Set<String> etagMdns = etags.keySet();
            List<String> externalMdns = new ArrayList<>(etagMdns);
            externalMdns.removeAll(membersMdn);
            knLogger.debug(methodName, " externalMdns :", externalMdns);
            if (!externalMdns.isEmpty()) {
                membersMdn.addAll(externalMdns);
            }
        }
        //Map<String, KnOPDirChgDTO> etagMap = updateSubscribersDirectroy(membersMdn, etags, persisterTxn);
        //Skipping this one and adding the logic below

        for (Map.Entry<String, KnOPDirChgDTO> DirDto : etags.entrySet()) {
            String mdn = DirDto.getKey().trim();
            KnOPDirChgDTO direcChngDto = etags.get(mdn);
            if (direcChngDto == null) {
                direcChngDto = new KnOPDirChgDTO();
            }
            direcChngDto.setDirUri(getDirectoryURI(mdn));
            //direcChngDto.setDirNewEtag(String.valueOf(etag));
            //direcChngDto.setDirPrevEtag(String.valueOf(--etag));
            etags.put(mdn, direcChngDto);
        }
        Map<String, KnCorpSubscriberDTO> subscrInfoMap = new HashMap<String, KnCorpSubscriberDTO>();
        if (membersMdn != null) {
            subscrInfoMap = subscrInfoDao.getSubsribersCorporateDetails(membersMdn, persisterTxn);
        }
        for (Map.Entry<String, KnOPDirChgDTO> entry : etags.entrySet()) {
            String mdn = entry.getKey();
            KnOPDirChgDTO directory = etags.get(mdn);
            KnOPDirChgDTO dir = entry.getValue();
            if (!isObjectNull(directory)) {
                if (!isObjectNull(dir)) {
                    Collection<KnOPDocChgDTO> intialDoc = dir.getDocChgDTO();
                    if (intialDoc == null) {
                        intialDoc = new ArrayList<KnOPDocChgDTO>();
                    }
                    KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
                    if (subsc != null) {
                        directory.setPocHome(subsc.getPocHome());
                        directory.setPresenceHome(subsc.getPresenceHome());
                        boolean notfnCapability = !upmDocNotify && subsc.isNotfnCapabiliy();
                        directory.setNotfnCapability(notfnCapability);
                        directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                        directory.setClientType(subsc.getClientType());
                        if (notfnCapability) {
                            boolean enabled76 = KnGeneralUtil.getFeatureBitValue(subsc.getSubsActiveFS2(), 76);
                            if (enabled76) directory.setNtfyOnAnyMDN(0);
                        }
                    }
                    directory.setDocChgDTO(intialDoc);
                }
            } else {
                if (!isObjectNull(dir)) {
                    KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
                    if (subsc != null) {
                        dir.setPocHome(subsc.getPocHome());
                        dir.setPresenceHome(subsc.getPresenceHome());
                        boolean notfnCapability = !upmDocNotify && subsc.isNotfnCapabiliy();
                        dir.setNotfnCapability(notfnCapability);
                        directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                        directory.setClientType(subsc.getClientType());
                        if (notfnCapability) {
                            boolean enabled76 = KnGeneralUtil.getFeatureBitValue(subsc.getSubsActiveFS2(), 76);
                            if (enabled76) dir.setNtfyOnAnyMDN(0);
                        }
                    }
                    etags.put(mdn, dir);
                }
            }
        }
        knLogger.debug(methodName, "mdnList size- ", getSize(mdnList), ", groupIdLst size - ", getSize(groupIdLst), "etags - ", KnGDPRTemplate.mapKeyMdn(etags), "EXIT Point.");
        return etags;
    }

    public Map<String, KnOPDirChgDTO> setPocHomeForProfileMdns(Collection<String> mdnList,
                                                               Collection<Integer> groupIdLst, Map<String, KnOPDirChgDTO> etags, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "setPocHomeForProfileMdns(Collection<String>, Collection<Integer>, Map<String, KnOPDirChgDTO> etags, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdnList size- ", getSize(mdnList), ", groupIdLst size - ",
                getSize(groupIdLst), "etags - ", KnGDPRTemplate.mapKeyMdn(etags));
        KnXDMSubscriberInfoDAO subscrInfoDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        //Collection<String> membersMdn = subscrInfoDao.getDistinctMembers(mdnList, groupIdLst, persisterTxn);
        Map<String, KnOPDirChgDTO> etagMap = updateSubscribersDirectroy(mdnList, etags, persisterTxn);
        Map<String, KnCorpSubscriberDTO> subscrInfoMap = new HashMap<String, KnCorpSubscriberDTO>();
        if (mdnList != null) {
            subscrInfoMap = subscrInfoDao.getSubsribersCorporateDetails(mdnList, persisterTxn);
        }
        for (Map.Entry<String, KnOPDirChgDTO> entry : etags.entrySet()) {
            String mdn = entry.getKey();
            KnOPDirChgDTO directory = etagMap.get(mdn);
            KnOPDirChgDTO dir = entry.getValue();
            if (!isObjectNull(directory)) {
                if (!isObjectNull(dir)) {
                    Collection<KnOPDocChgDTO> intialDoc = dir.getDocChgDTO();
                    if (intialDoc == null) {
                        intialDoc = new ArrayList<KnOPDocChgDTO>();
                    }
                    KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
                    if (subsc != null) {
                        directory.setPocHome(subsc.getPocHome());
                        directory.setPresenceHome(subsc.getPresenceHome());
                        directory.setNotfnCapability(subsc.isNotfnCapabiliy());
                        directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                        directory.setClientType(subsc.getClientType());
                    }
                    directory.setDocChgDTO(intialDoc);
                }
            } else {
                if (!isObjectNull(dir)) {
                    KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
                    if (subsc != null) {
                        dir.setPocHome(subsc.getPocHome());
                        dir.setPresenceHome(subsc.getPresenceHome());
                        dir.setNotfnCapability(subsc.isNotfnCapabiliy());
                        directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                        directory.setClientType(subsc.getClientType());
                    }
                    etagMap.put(mdn, dir);
                }
            }
        }
        knLogger.debug(methodName, "mdnList size- ", getSize(mdnList), ", groupIdLst size - ", getSize(groupIdLst),
                "etags - ", KnGDPRTemplate.mapKeyMdn(etags), "EXIT Point.", KnGDPRTemplate.mapKeyMdn(etagMap));
        return etagMap;
    }


    public Map<String, KnOPDirChgDTO> updateSubscribersDirectroy(Collection<String> membersMdn,
                                                                 Map<String, KnOPDirChgDTO> etags,
                                                                 KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "updateSubscribersDirectroy(Collection<String>, Map<String, KnOPDirChgDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point :");
        KnXDMDirectoryDAO directoryDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMDirectoryDAO(pttServerId);
        Map<String, KnOPDirChgDTO> currdirectoryEtag = directoryDAO.updateEtag(membersMdn, persisterTxn, etags);
        KnXDMSubscriberInfoDAO subscrInfoDao =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        Map<String, KnCorpSubscriberDTO> subscrInfoMap = subscrInfoDao.getSubsribersCorporateDetails(membersMdn, persisterTxn);
        for (String mdn : membersMdn) {
            mdn = mdn.trim();
            KnOPDirChgDTO directory = currdirectoryEtag.get(mdn);
            if (directory == null) {
                directory = new KnOPDirChgDTO();
            }
            KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
            if (subsc != null) {
                directory.setPocHome(subsc.getPocHome());
                directory.setPresenceHome(subsc.getPresenceHome());
                directory.setProtoVersion(String.valueOf(subsc.getClientPVmajorVer()));
            }
        }
        knLogger.debug(methodName, "EXIT Point.");
        return currdirectoryEtag;
    }

    public int getSublistCountByNameExcludingCurrentSublist(int corpId, String sublistName, int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSublistCountByNameExcludingCurrentSublist(int, String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnXDMCorpListInfoDAO corpListInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        int count = corpListInfoDAO.getSublistCountByNameExcludingCurrentSublist(corpId, sublistName, sublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return count;
    }

    public Set<Integer> getCorpGroupIdByOsmListId(int OsmListId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpGroupIdByOsmListId(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        Set<Integer> groupIds = corpGroupInfoDAO.getCorpGroupIdByOsmListId(OsmListId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return groupIds;
    }

    public int updateIsOSMAuthorize(Set<Integer> groupIds, String mdn, String isOSMAuthorize, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateIsOSMAuthorize()";
        knLogger.debug(methodName, "ENTRY: ");
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        knLogger.debug(methodName, "EXIT Point.");
        return corpGroupInfoDAO.updateIsOSMAuthorize(groupIds, mdn, isOSMAuthorize, persisterTxn);

    }

    public Map<Integer, Integer> updateGroupListEtag(Collection<Integer> groupIdLst, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        return corpGroupInfoDAO.updateGroupListEtag(groupIdLst, persisterTxn);
    }

    public Map<Integer, Integer> updateGroupListEtag(Map<Integer, Integer> groupIdLst, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateGroupListEtag(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        knLogger.debug(methodName, "EXIT Point.");
        return corpGroupInfoDAO.updateGroupListEtag(groupIdLst, persisterTxn);
    }

    public List<Integer> getGroupHavingMember(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        return groupMemberListDAO.getGroupHavingMember(contactDTO, persisterTxn);
    }


    public List<String> getCorpResourceList(KnIPCorpContactDTO contactDTO, int maxContacts, String
            pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpContactListDAO contactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        return contactListDAO.getCorpResourceList(contactDTO, maxContacts, readOnly, persisterTxn);
    }

    public List<String> getCorpCommonContactList(String mdn, int maxContacts, String
            pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpContactListDAO contactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        return contactListDAO.getCorpCommonContactList(mdn, maxContacts, readOnly, persisterTxn);
    }

    public Set<String> getCorpNonCommonContactList(String mdn, int maxContacts, String
            pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpContactListDAO contactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        return contactListDAO.getCorpNonCommonContactList(mdn, maxContacts, readOnly, persisterTxn);
    }

    public Collection<KnCorpSubscriberDTO> getGroupsAllMemberList(int groupId, int maxGroupMemberLimit, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getGroupsAllMemberList(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        Collection<KnCorpSubscriberDTO> memberList = groupMemberListDAO.getGroupsAllMemberList(groupId,
                maxGroupMemberLimit, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return memberList;
    }

    public KnCorpGroupDTO getGroupBasicInfo(int groupId, boolean readonly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getGroupBasicInfo(groupId, readonly, persisterTxn);
    }

    public Integer getMemberCountFromMemberList(int groupId, boolean readonly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getMemberCountFromMemberList(groupId, readonly, persisterTxn);
    }

    public Map<Integer, Collection<String>> getSubcriberSublistMemberShipList(Collection<String> mdnList, int corpId,
                                                                              KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getSubcriberSublistMemberShipList(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        Map<Integer, Collection<String>> sublistListsMemberMap = null;
        KnXDMCorpListMemberDAO corpMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        sublistListsMemberMap = corpMemberListDAO.getSubcriberSublistMemberShipList(mdnList, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT ");
        return sublistListsMemberMap;
    }

    public Map<Integer, HashMap<String, Collection<String>>> getAllSubscribersGroupList(Collection<String> mdnList,
                                                                                        int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getAllSubscribersGroupList(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        Map<Integer, HashMap<String, Collection<String>>> groupMemberMap = null;
        KnXDMCorpGroupDistInfoDAO corpMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupDistInfoDAO(pttServerId);
        groupMemberMap = corpMemberListDAO.getAllSubscribersGroupList(mdnList, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point Group Members returned - ", groupMemberMap);
        return groupMemberMap;
    }


    public Collection<String> getMappedSubscribersContactList(Collection<String> mdnList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getMappedSubscribersContactList(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        Collection<String> subscriberLists = null;
        KnXDMCorpContactListDAO corpContactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        subscriberLists = corpContactListDAO.getMappedSubscribersContactList(mdnList, persisterTxn);
        knLogger.debug(methodName, "EXIT ");
        return subscriberLists;
    }

    public void deleteMembersFromAllSublist(Map<Integer, Collection<String>> sublistMemberMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteMembersFromAllSublist(Map<Integer, Collection<String>>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpListMemberDAO corpMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        corpMemberListDAO.deleteMembersFromAllSublist(sublistMemberMap, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public int getExternalContactCount(int corpId, int contactType, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMExtPoCSubscriberDAO extPoCSubscriberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(pttServerId);
        return extPoCSubscriberDAO.getExternalContactCount(corpId, contactType, persisterTxn);
    }

    public void deleteExtMember(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteExtMember(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnXDMExtPoCSubscriberDAO extPoCSubscriberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(pttServerId);
        extPoCSubscriberDAO.deleteExtMember(mdnList, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void removeSublistListsMappingFromGroup(Collection<Integer> removedSublistIds, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "removeSublistListsMappingFromGroup(Collection<Integer>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupListRefDAO corpGroupListRefDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupListRefDAO(pttServerId);
        corpGroupListRefDAO.removeSublistListsMappingFromGroup(removedSublistIds, groupId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
    }

    public int getGroupPrivateListId(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupPrivateListId(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        int privateListId = 0;

        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        privateListId = corpGroupInfoDAO.getGroupPrivateListId(groupId, persisterTxn);

        knLogger.debug(methodName, "Group private list id - ", privateListId);
        return privateListId;

    }

    public void deleteCorpGroupMemberCountEntry(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteCorpGroupMemberCountEntry(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupMemberCountDAO corpGroupMemberCount = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberCountDAO(pttServerId);
        corpGroupMemberCount.deleteCorpGroupMemberCountEntry(groupId, persisterTxn);

        knLogger.debug(methodName, "Group Memeber Count entry removed.");

    }

    public Map<Integer, Collection<KnCorpSubscriberDTO>> getSublistListDistributionList(Collection<Integer> sublistList,
                                                                                        KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSublistListDistributionList(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");

        KnXDMCorpListDistInfoDAO corpListDistInfo = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListDistInfoDAO(pttServerId);
        Map<Integer, Collection<KnCorpSubscriberDTO>> distInfo = corpListDistInfo.getSublistListDistributionList(sublistList,
                persisterTxn);

        knLogger.debug(methodName, "Group Memeber Count entry removed.");
        return distInfo;

    }

    public void insertMemberInAllSublists(Map<Integer, Collection<String>>
                                                  sublistMemberMap, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertMemberInAllSublists(Map<Integer, Collection<String>>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpListMemberDAO corpMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        corpMemberListDAO.insertMemberInAllSublists(sublistMemberMap, corpId, persisterTxn);
        knLogger.debug(methodName, "Group Memeber Count entry removed.");

    }


    public LinkedList<String> getGroupMemberList(int groupId,
                                                 String xdmsHome,
                                                 KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupMemberList(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupDistInfoDAO corpGroupDistInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupDistInfoDAO(pttServerId);
        LinkedList<String> mdnList = corpGroupDistInfoDAO.getGroupMemberList(groupId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return mdnList;
    }

    public void modifyGroupVideoPermission(Integer videoPermission, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyGroupVideoPermission(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.modifyGroupVideoPermission(videoPermission, groupId, persisterTxn);
        knLogger.debug(methodName, "modifyGroupVideoPermission modified successfully");
    }
    public void modifyGroupName(String groupName, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyGroupName(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.modifyGroupName(groupName, groupId, persisterTxn);
        knLogger.debug(methodName, "Group Name modified successfully");
    }


    public void modifyGroupAvatar(Integer avatar, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyGroupAvatar(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.modifyGroupAvatar(avatar, groupId, persisterTxn);
        knLogger.debug(methodName, "Group Name modified successfully");
    }

    public void modifyGroupOSMListId(int corpId, int groupId, String OSMListId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyGroupOSMListId(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.modifyGroupOSMListId(corpId, groupId, OSMListId, persisterTxn);
        knLogger.debug(methodName, "Group Name modified successfully");
    }


    public void deleteAllGroupsSublistRef(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAllGroupsSublistRef(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupListRefDAO listRef =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupListRefDAO(pttServerId);
        listRef.deleteAllGroupsSublistRef(groupIdsList, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void deleteAllGroupsDistribution(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteAllGroupsDistribution(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnCorpGroupDistInfoDAO groupDistInfo =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpGroupDistInfoDAO(pttServerId);
        groupDistInfo.deleteAllGroupsDistribution(groupIdsList, persisterTxn);
        knLogger.debug(methodName, "EXIT :");
    }

    public void deleteAllGroupsCorpGroupMemberCountEntry(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAllGroupsCorpGroupMemberCountEntry(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupMemberCountDAO corpGroupMemberCount = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberCountDAO(pttServerId);
        corpGroupMemberCount.deleteAllGroupsCorpGroupMemberCountEntry(groupIdsList, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void deleteAllGroups(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        groupInfoDAO.deleteAllGroup(groupIdsList, persisterTxn);
    }

    public void deleteAllGrpHierarchy(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        groupInfoDAO.deleteAllGrpHierarchy(groupIdsList, persisterTxn);
    }


    public Collection<Integer> getCorpSublistIdList(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpSublistIdList(KnIPCorpInfoDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpListInfoDAO corpListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListInfoDAO(pttServerId);
        Collection<Integer> sublistIdList = corpListDAO.getCorpSublistIdList(corpInfoDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT :");
        return sublistIdList;
    }

    public void deleteAllSublist(Collection<Integer> sublistIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteAllSublist(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry sublistIdsList:", sublistIdsList);
        //DELETE FROM DG.CORPLISTMEMBER WHERE CORPLISTID IN (SUBLISTID)
        deleteAllCorpSublistMembers(sublistIdsList, persisterTxn);
        updateSublistsSubscribersContactCount(sublistIdsList, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED,
                KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED, persisterTxn, KnConstants.MAX_LIMIT_VALIDATION_NOT_REQUIRED);
        //DELETE FROM DG.CORPLISTDISTINFO WHERE CORPLISTID IN (SUBLISTID)
        deleteAllCorpListDistReference(sublistIdsList, persisterTxn);
        //DELETE FROM DG.CORPGROUP_LISTREF WHERE CORPLISTID IN (SUBLISTID)
        deleteAllCorpGroupListRef(sublistIdsList, persisterTxn);
        //DELETE FROM DG.CORPLISTINFO WHERE CORPLISTID IN (SUBLISTID)
        deleteAllSublistInfo(sublistIdsList, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    private void deleteAllCorpGroupListRef(Collection<Integer> sublistIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteAllCorpGroupListRef(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry: ");
        KnCorpGroupListRefDAO corpGroupListRefDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpGroupListRefDAO(pttServerId);
        corpGroupListRefDAO.deleteAllCorpGroupListRef(sublistIdsList, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    private void deleteAllSublistInfo(Collection<Integer> sublistIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteAllSublistInfo(Collection<Integer>,KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        corpListDAO.deleteAllSublistInfo(sublistIdsList, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    private void deleteAllCorpListDistReference(Collection<Integer> sublistIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteAllCorpListDistReference(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry: ");
        KnXDMCorpListDistInfoDAO corpListDistDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListDistInfoDAO(pttServerId);
        corpListDistDAO.deleteAllCorpListDistReference(sublistIdsList, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void deleteAllCorpSublistMembers(Collection<Integer> sublistIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteAllCorpSublistMembers(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpListMemberDAO corpListMemDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        corpListMemDAO.deleteAllCorpSublistMembers(sublistIdsList, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void deleteCorporateExternalMembers(KnIPCorpInfoDTO corpInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteCorporateExternalMembers(KnIPCorpInfoDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnXDMExtPoCSubscriberDAO extPoCSubscriberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(pttServerId);
        extPoCSubscriberDAO.deleteCorporateExternalMembers(corpInfoDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT Point External Member Deleted for Corporate.");
    }

    public int getCorpSubscriberCount(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpSubscriberCount(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        int count = 0;
        KnXDMSubscriberInfoDAO poCSubscriberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        count = poCSubscriberDAO.getCorpSubscriberCount(corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point");
        return count;
    }

    public void nullifyContactCorpIdInExtTable(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "nullifyContactCorpIdInExtTable(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMExtPoCSubscriberDAO extPoCSubscriberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(pttServerId);
        extPoCSubscriberDAO.nullifyContactCorpIdInExtTable(corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point");
    }

    public Collection<String> getFinalMemberGroupContactCount(Collection<Integer> sublistMappedToGroup, Collection<KnCorpSubscriberDTO> privateMemberList,
                                                              Collection<KnCorpSubscriberDTO> mdnsToBeAddedToPrivateList,
                                                              KnMdnDetailsPersistDTO contactMdnPersistDto,
                                                              KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getFinalMemberGroupContactCount(Collection<Integer>, Collection<KnCorpSubscriberDTO>," +
                " Collection<KnCorpSubscriberDTO> , KnMdnDetailsPersistDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListMemberDAO corpListMemberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        Collection<String> mdnList = corpListMemberDAO.getFinalMemberGroupContactCount(sublistMappedToGroup, privateMemberList,
                mdnsToBeAddedToPrivateList, contactMdnPersistDto, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return mdnList;
    }

    public void insertMembersIntoCorpContactList(Map<String, Collection<KnCorpSubscriberDTO>> mdnContactListMap,
                                                 KnPersisterTxn persisterTxn) throws KnDAOException {

        final String methodName = "insertMembersIntoCorpContactList(Map<String, Collection<KnCorpSubscriberDTO>>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : ");
        KnXDMCorpContactListDAO corpContactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        corpContactListDAO.insertMembersIntoCorpContactList(mdnContactListMap, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
    }

    public LinkedHashMap<String, LinkedList<Integer>> deleteMembersFromCorpContactList(LinkedHashMap<String, LinkedList<String>> mdnContactListMap,
                                                                                       KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteMembersFromCorpContactList(Map<String, Collection<String>>,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : ");
        KnXDMCorpContactListDAO corpContactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        return corpContactListDAO.deleteMembersFromCorpContactList(mdnContactListMap, persisterTxn);
    }

    public Map<String, Collection<String>> getSubscribersContactList(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpContactListDAO corpContactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        return corpContactListDAO.getSubscribersContactList(mdnList, persisterTxn);
    }


    public void insertIntoCorpGroupMemberList(Map<Integer, Collection<KnCorpContactDTO>> groupMemberList,
                                              KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertIntoCorpGroupMemberList(Map<Integer, Collection<KnCorpContactDTO>>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : ");
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        groupMemberListDAO.insertIntoCorpGroupMemberList(groupMemberList, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void insertBulkIntoCorpGroupMemberList(Map<Integer, KnCorpContactDTO> groupMemberList,
                                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "insertNulkIntoCorpGroupMemberList(Map<Integer, Collection<KnCorpContactDTO>>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : ");
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        groupMemberListDAO.insertBulkIntoCorpGroupMemberList(groupMemberList, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public LinkedHashMap<Integer, LinkedList<Integer>> deleteCorpGroupMemberList(LinkedHashMap<Integer, LinkedList<String>> groupMemberList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        return groupMemberListDAO.deleteCorpGroupMemberList(groupMemberList, persisterTxn);
    }

    public Map<Integer, Collection<String>> selectGroupMemberListForGroupIds(Collection<Integer> groupList,
                                                                             KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "selectGroupMemberListForGroupIds(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : ");
        Map<Integer, Collection<String>> groupMemberListMap;
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        groupMemberListMap = groupMemberListDAO.selectGroupMemberListForGroupIds(groupList, persisterTxn);
        knLogger.debug(methodName, "EXIT ");
        return groupMemberListMap;
    }

    public int getGroupMemSize(Collection<Integer> groupList, KnPersisterTxn persisterTxn) throws KnDAOException {
        int count = 0;
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        count = groupMemberListDAO.getGroupMemSize(groupList, persisterTxn);
        return count;
    }

    public int getMCXGroupMemSize(Collection<Integer> groupList, KnPersisterTxn persisterTxn) throws KnDAOException {
        int count = 0;
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        count = groupMemberListDAO.getMCXGroupMemSize(groupList, persisterTxn);
        return count;
    }

    public void deleteSublistRefAndSublist(int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteSublistRefAndSublist(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        deleteCorpListDistReference(sublistId, persisterTxn);
        deleteCorpListDistGroupReference(sublistId, persisterTxn);
        deleteSublistInfo(sublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void deleteAllCorpGroupMemberList(Collection<Integer> groupIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteAllCorpGroupMemberList(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        groupMemberListDAO.deleteAllCorpGroupMemberList(groupIdList, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public List<String> getAllGroupMdns(Collection<Integer> groupIdList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getAllGroupMdns(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        List<String> allGroupMdns = groupMemberListDAO.getAllGroupMdns(groupIdList, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return allGroupMdns;
    }
    //update the corpcontactlist table with new mdn data
    public void updateOwnerMdnInContactList(KnIPCorpContactDTO contactDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateOwnerMdnInContactList(KnIPCorpContactDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpContactListDAO corpContactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        corpContactListDAO.updateOwnerMdnInContactList(contactDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }


    public void deleteSubscribersContactList(String ownerMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteSubscribersContactList(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpContactListDAO corpContactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        corpContactListDAO.deleteSubscribersContactList(ownerMdn, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void deleteBulkSubscribersContactList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteBulkSubscribersContactList(String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpContactListDAO corpContactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        corpContactListDAO.deleteBulkSubscribersContactList(mdnList, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public KnCorpGroupInfoPersistDTO selectGroupBasicInfo(int groupId, int corpId, int clientIntf, KnPersisterTxn persisterTxn,Boolean hiearchyCall) throws KnDAOException {
        String methodName = "selectGroupBasicInfo(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        //retrieve group info
        KnCorpGroupInfoPersistDTO groupPersistDTO = corpGroupInfoDAO.selectGroupBasicInfo(groupId, corpId, clientIntf, persisterTxn,hiearchyCall);

        return groupPersistDTO;

    }
    public KnCorpGroupInfoPersistDTO getGroupBasicInfoDetailsWithoutCorpId(int groupId, int clientIntf, KnPersisterTxn persisterTxn,Boolean hiearchyCall) throws KnDAOException {
        String methodName = "getGroupBasicInfoDetailsWithoutCorpId(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        //retrieve group info
        KnCorpGroupInfoPersistDTO groupPersistDTO = corpGroupInfoDAO.getGroupBasicInfoDetailsWithoutCorpId(groupId, clientIntf, persisterTxn,hiearchyCall);

        return groupPersistDTO;

    }

    public Collection<Integer> getGroupsSublistListFromDB(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupsSublistListFromDB(int,  KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpGroupListRefDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupListRefDAO(pttServerId);
        //retrieve group info
        Collection<Integer> sublistLists = corpGroupInfoDAO.getGroupsSublistListFromDB(groupId, persisterTxn);

        return sublistLists;

    }

    public Collection<KnCorpSublistDTO> getSubscMappedSublistListWithMemberCount(KnIPCorpContactDTO contactDTO,
                                                                                 int corpListId, boolean readOnly,
                                                                                 KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getSubscMappedSublistListWithMemberCount(KnIPCorpContactDTO, int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : contactDTO - ", contactDTO, ", corpListId - ", corpListId);
        KnXDMCorpListDistInfoDAO corpListDistInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListDistInfoDAO(pttServerId);
        //retrieve group info
        Collection<KnCorpSublistDTO> sublistLists =
                corpListDistInfoDAO.getSubscMappedSublistListWithMemberCount(contactDTO, corpListId, readOnly, persisterTxn);

        return sublistLists;

    }

    public Map<String, KnCorpSubscriberDTO> getPoCSubscriberExistMap(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPoCSubscriberExistList(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        //retrieve subsc info
        Map<String, KnCorpSubscriberDTO> subsclistMap =
                subscriberTable.getPoCSubscriberExistMap(mdnList, persisterTxn);

        return subsclistMap;

    }

    /**
     * This method is used to fetch the supervisor of the group with is_supervisory as 1 or 2
     *
     * @param groupId
     * @param persisterTxn
     * @return
     */
    public Map<String, KnCorpGroupMemberDTO> getGroupSupervisorMembers(int groupId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO goupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return goupMemberListDAO.getGroupSupervisorMembers(groupId, readOnly, persisterTxn);
    }

    public Map<String, Integer> getExternalSubscriberGroupCount(Collection<String> externalMdnList,
                                                                int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExternalSubscriberGroupCount(Collection<String>, int, KnPersisterTxn)";
        KnXDMCorpGroupMemberListDAO goupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        //retrieve subsc info
        Map<String, Integer> externalSubGrpCountMap =
                goupMemberListDAO.getExternalSubscriberGroupCount(externalMdnList, corpId, persisterTxn);
        return externalSubGrpCountMap;
    }

    public void updateGroupMemberListSupervisorList(Collection<KnCorpGroupMemberDTO> supervisorMemberList,
                                                    int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateGroupMemberListSupervisorList(Collection<KnCorpGroupMemberDTO>, int, KnPersisterTxn)";
        KnXDMCorpGroupMemberListDAO goupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        goupMemberListDAO.updateGroupMemberListSupervisorList(supervisorMemberList, groupId, persisterTxn);
    }

    public Map<String, HashMap<Integer, String>> getGroupListStatus(Collection<Integer> groupIdLst, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupListStatus(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpGroupMemberListDAO groupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        //retrieve subsc info
        Map<String, HashMap<Integer, String>> groupListStatusMap =
                groupMemberListDAO.getGroupListStatus(groupIdLst, persisterTxn);
        return groupListStatusMap;

    }

    public ArrayList<Integer> getSharedSublistFromList(Collection<Integer> sublistLists, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSharedSublistFromList(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry: ");
        KnXDMCorpListInfoDAO corpListInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListInfoDAO(pttServerId);
        //retrieve subsc info
        ArrayList<Integer> sharedSublist =
                corpListInfoDAO.getSharedSublistFromList(sublistLists, persisterTxn);
        return sharedSublist;

    }

    public ArrayList<Integer> getNonSharedSublistFromList(Collection<Integer> sublistLists, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getNonSharedSublistFromList(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry: ");
        KnXDMCorpListInfoDAO corpListInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListInfoDAO(pttServerId);
        //retrieve subsc info
        ArrayList<Integer> sharedSublist =
                corpListInfoDAO.getNonSharedSublistFromList(sublistLists, persisterTxn);
        return sharedSublist;

    }

    public ArrayList<Integer> getEmptySublistFrmList(ArrayList<Integer> sharedSublists, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getEmptySublistFrmList(ArrayList<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpListMemberDAO corpListMemberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        //retrieve subsc info
        ArrayList<Integer> sharedSublist =
                corpListMemberDAO.getEmptySublistFrmList(sharedSublists, persisterTxn);
        return sharedSublist;
    }

    public void updateSusbcribersCorpIdInImpactedTables(String corpId, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSusbcribersCorpIdInImpactedTables(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        updateCorpListMemberCorpId(corpId, mdn, persisterTxn);
        updateCorpContactListContactMdnCorpId(corpId, mdn, persisterTxn);
        updateExtenalContactCorpId(corpId, mdn, persisterTxn);
    }

    private void updateExtenalContactCorpId(String corpId, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateExtenalContactCorpId(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMExtPoCSubscriberDAO extSubscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMExtPoCSubscriberDAO(pttServerId);
        //retrieve subsc info
        extSubscDAO.updateExtContactCorpId(corpId, mdn, persisterTxn);
    }

    private void updateCorpContactListContactMdnCorpId(String corpId, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorpContactListContactMdnCorpId(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpContactListDAO corpListMemberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpContactListDAO(pttServerId);
        //retrieve subsc info
        corpListMemberDAO.updateContactCorpId(corpId, mdn, persisterTxn);
    }

    private void updateCorpListMemberCorpId(String corpId, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateCorpListMemberCorpId(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpListMemberDAO corpListMemberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        //retrieve subsc info
        corpListMemberDAO.updateMemberCorpId(corpId, mdn, persisterTxn);
    }

    public ArrayList<Integer> getAllGroupsPrivateList(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getAllGroupsPrivateList(Collection<Integer> ,  KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        //retrieve subsc info
        ArrayList<Integer> sublistList = corpGroupInfoDAO.getAllGroupsPrivateList(groupIdsList, persisterTxn);
        return sublistList;
    }

    public void deleteAllSublistInfo(ArrayList<Integer> privateGroupSublistList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteAllSublistInfo(ArrayList<Integer>,  KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpListInfoDAO listInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListInfoDAO(pttServerId);
        //retrieve subsc info
        listInfoDAO.deleteAllSublistInfo(privateGroupSublistList, persisterTxn);
    }


    public Map<String, Integer> getSubscribersPrivateListId(Collection<String> completeMdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscribersPrivateListId(completeMdnList,  persisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMSubscriberInfoDAO subscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        //retrieve subsc info
        Map<String, Integer> subscPrivateListMap = subscDAO.getSubscribersPrivateListId(completeMdnList, persisterTxn);
        return subscPrivateListMap;
    }

    public Map<String, Collection<KnCorpSubscriberDTO>> getSubscribersPrivateList(Map<String, Integer> subscPrivateListMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscribersPrivateList(Map<String, Integer>,  KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpListMemberDAO listMemberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        //retrieve subsc info
        Map<String, Collection<KnCorpSubscriberDTO>> privatListMap = listMemberDAO.getSubscribersPrivateList(subscPrivateListMap, persisterTxn);
        return privatListMap;
    }


    public void insertCorplistMembers(Map<Integer, Collection<KnCorpSubscriberDTO>> listMemberMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertCorplistMembers(Map<Integer, Collection<KnCorpSubscriberDTO>>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpListMemberDAO listMemberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        //retrieve subsc info
        listMemberDAO.insertCorplistMembers(listMemberMap, persisterTxn);
    }

    public void insertCorpContactListMembers(Map<String, Collection<KnCorpSubscriberDTO>> memberOfPrivateList,
                                             KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertCorpContactListMembers(Map<String, Collection<KnCorpSubscriberDTO>>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpContactListDAO contactListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpContactListDAO(pttServerId);
        //retrieve subsc info
        contactListDAO.insertCorpContactListMembers(memberOfPrivateList, persisterTxn);
    }

    public LinkedHashMap<Integer, LinkedList<String>> getGroupDispatcherSubscriber(Collection<Integer> groupIdsList, int supervisor, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupDispatcherSubscriber(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpGroupMemberListDAO groupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        //retrieve subsc info
        LinkedHashMap<Integer, LinkedList<String>> dispatcherMap = groupMemberListDAO.getGroupDispatcherSubscriber(groupIdsList, supervisor, persisterTxn);
        return dispatcherMap;

    }

    public LinkedHashMap<Integer, LinkedList<String>> getGroupMdnSubscriber(Collection<Integer> groupIdsList, int memberType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupMdnSubscriber(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpGroupMemberListDAO groupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        //retrieve subsc info
        LinkedHashMap<Integer, LinkedList<String>> groupMdnMap = groupMemberListDAO.getGroupMdnSubscriber(groupIdsList, memberType, persisterTxn);
        return groupMdnMap;

    }

    public Map<Integer, Collection<String>> getGroupLocWatcherSubscriber(Collection<Integer> groupIdList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getGroupLocWatcherSubscriber(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpGroupMemberListDAO groupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        Map<Integer, Collection<String>> dispatcherMap = groupMemberListDAO.getGroupLocWatcherSubscriber(groupIdList, persisterTxn);
        return dispatcherMap;
    }

    public void updateGroupType(int groupType, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateGroupType(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        //retrieve subsc info
        groupInfoDAO.updateGroupType(groupType, groupId, persisterTxn);
    }

    public Map<Integer, Collection<String>> getSubcriberSublistMemberShipListForAllCorporate(Collection<String> mdnList,
                                                                                             KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getSubcriberSublistMemberShipListForAllCorporate(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        Map<Integer, Collection<String>> sublistListsMemberMapForAllCorp = null;
        KnXDMCorpListMemberDAO corpMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        sublistListsMemberMapForAllCorp = corpMemberListDAO.getSubcriberSublistMemberShipListForAllCorporate(mdnList, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return sublistListsMemberMapForAllCorp;
    }

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getAllSubscribersGroupListForAllCorporate(Collection<String> mdnList,
                                                                                                    KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getAllSubscribersGroupListForAllCorporate(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        Map<Integer, Collection<KnCorpGroupMemberDTO>> groupMemberMap = null;
        KnXDMCorpGroupDistInfoDAO corpMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupDistInfoDAO(pttServerId);
        groupMemberMap = corpMemberListDAO.getAllSubscribersGroupListForAllCorporate(mdnList, persisterTxn);
        knLogger.debug(methodName, "EXIT :");
        return groupMemberMap;
    }

    public void deleteSusbcribersFromExtContactTables(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteSusbcribersFromExtContactTables(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMExtPoCSubscriberDAO extSubscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMExtPoCSubscriberDAO(pttServerId);
        //retrieve subsc info
        extSubscDAO.deleteSusbcribersFromExtContactTables(mdn, persisterTxn);
    }

    public void updateCorpGroupMemberList(Collection<Integer> groupList, String oldMdn, String newMdn, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        final String methodName = "updateCorpGroupMemberList(Collection<Integer>, String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : ");
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        groupMemberListDAO.updateCorpGroupMemberList(groupList, oldMdn, newMdn, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public Map<Integer, Collection<String>> getSubcriberSublistMemberShipListAsExtContact(Collection<String> mdnList, int corpId,
                                                                                          KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getSubcriberSublistMemberShipListAsExtContact(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        Map<Integer, Collection<String>> sublistListsMemberMap = null;
        KnXDMCorpListMemberDAO corpMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        sublistListsMemberMap = corpMemberListDAO.getSubcriberSublistMemberShipListAsExtContact(mdnList, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point");
        return sublistListsMemberMap;
    }

    public Map<Integer, Collection<String>> getAllSubscribersGroupListAsExtContact(Collection<String> mdnList,
                                                                                   int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getAllSubscribersGroupListAsExtContact(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        Map<Integer, Collection<String>> groupMemberMap = null;
        KnXDMCorpGroupDistInfoDAO corpMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupDistInfoDAO(pttServerId);
        groupMemberMap = corpMemberListDAO.getAllSubscribersGroupListAsExtContact(mdnList, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point ");
        return groupMemberMap;
    }

    public Map<Integer, Collection<String>> getGroupSubscriberDistList(Collection<Integer> grpIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupSubscriberDistList(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpGroupDistInfoDAO groupDistInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupDistInfoDAO(pttServerId);
        //retrieve subsc info
        Map<Integer, Collection<String>> subscDistInfo = groupDistInfoDAO.getGroupSubscriberDistList(grpIdList, persisterTxn);
        return subscDistInfo;
    }

    public void insertIntoCorpGroupDistInfo(Map<Integer, Map<String, Collection<String>>> adddedmembers, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertIntoCorpGroupDistInfo(Map<Integer, Map<String, Collection<String>>>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpGroupDistInfoDAO groupDistInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupDistInfoDAO(pttServerId);
        //retrieve subsc info
        groupDistInfoDAO.insertIntoCorpGroupDistInfo(adddedmembers, persisterTxn);
    }

    public void insertIntoCorpGrpDistInfo(Map<Integer, String> adddedmembers, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertIntoCorpGrpDistInfo(Map<Integer, Map<String, Collection<String>>>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpGroupDistInfoDAO groupDistInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupDistInfoDAO(pttServerId);
        //retrieve subsc info
        groupDistInfoDAO.insertIntoCorpGrpDistInfo(adddedmembers, persisterTxn);
    }

    public void deleteFrmCorpGroupDistInfo(Map<Integer, Map<String, Collection<String>>> deletedmembers, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteFrmCorpGroupDistInfo(Map<Integer, Map<String, Collection<String>>>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpGroupDistInfoDAO groupDistInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupDistInfoDAO(pttServerId);
        //retrieve subsc info
        groupDistInfoDAO.deleteFrmCorpGroupDistInfo(deletedmembers, persisterTxn);
    }

    public void addExternalContactsInAllCorp(Map<Integer, KnCorpSubscriberDTO> corpIdExtContactNameMap, String newMdn, int corpId, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        String methodName = "addExternalContactsInAllCorp(Map<Integer, String>, String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMExtPoCSubscriberDAO extSubscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMExtPoCSubscriberDAO(pttServerId);
        //retrieve subsc info
        extSubscDAO.addExternalContactsInAllCorp(corpIdExtContactNameMap, newMdn, corpId, persisterTxn);
    }


    public void insetActivationCode(String mdn, String activationCode, Timestamp
            expiryTime, KnPersisterTxn persisterTxn, int clientType, Timestamp currentTimeInUTC) throws KnDAOException {
        String methodName = "insetActivationCode(String, String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpActivationDAO activationDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpActivationDAO(pttServerId);
        activationDao.insetActivationCode(mdn, activationCode, expiryTime, persisterTxn, clientType, currentTimeInUTC);
    }

    public Collection<KnCorpSubscriberDTO> insertActivationCode(Collection<KnCorpSubscriberDTO> subscList, String serviceName, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertActivationCode(Collection, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpActivationDAO activationDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpActivationDAO(pttServerId);
        return activationDao.insertActivationCode(subscList, serviceName, persisterTxn);
    }

    public boolean isActivationCodeExist(String mdn, KnPersisterTxn persisterTxn, int clientType) throws KnDAOException {
        String methodName = "isActivationCodeExist(String, KnPersisterTxn, int)";
        knLogger.debug(methodName, "Entry Point :");
        boolean status = false;
        KnXDMCorpActivationDAO activationDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpActivationDAO(pttServerId);
        status = activationDao.isActivationCodeExist(mdn, persisterTxn, clientType);

        knLogger.debug(methodName, "Exit : status - ", status);
        return status;
    }

    public Set<String> isActivationCodeExistInDB(Set<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isActivationCodeExistInDB(Set, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpActivationDAO activationDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpActivationDAO(pttServerId);
        return activationDao.isActivationCodeExistInDB(mdnList, readOnly, persisterTxn);
    }

    public Collection<KnCorpSubscriberDTO> isActivationCodeExistForMDN(Collection<KnCorpSubscriberDTO> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isActivationCodeExistForMDN(Collection, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        Collection<KnCorpSubscriberDTO> subsList;
        KnXDMCorpActivationDAO activationDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpActivationDAO(pttServerId);
        subsList = activationDao.isActivationCodeExistForMDN(mdnList, readOnly, persisterTxn);
        knLogger.debug(methodName, "Exit : subsList - ", subsList.toString());
        return subsList;
    }

    public void updateActivationCode(String mdn, String activationCode, Timestamp
            expiryTime, KnPersisterTxn persisterTxn, int clientType, Timestamp currentTimeInUTC) throws KnDAOException {
        String methodName = "updateActivationCode(String, String, String, KnPersisterTxn, int)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpActivationDAO activationDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpActivationDAO(pttServerId);
        activationDao.updateActivationCode(mdn, activationCode, expiryTime, persisterTxn, clientType, currentTimeInUTC);
    }

    public Collection<KnCorpSubscriberDTO> updateActivationCode(Collection<KnCorpSubscriberDTO> subsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateActivationCode(Collection, Timestamp, KnPersisterTxn, Timestamp)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpActivationDAO activationDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpActivationDAO(pttServerId);
        return activationDao.updateActivationCode(subsList, persisterTxn);
    }

    public void updateSubsAuthStatusToProvisioningStat(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubsAuthStatusToProvisioningStat(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        subscriberTable.updateSubsAuthStatusToProvisioningStat(mdn, persisterTxn);
    }

    public void invalidatePocUserPassword(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "invalidatePocUserPassword(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        subscriberTable.invalidatePocUserPassword(mdn, persisterTxn);
    }

    public Collection<String> getSubsNotInDispatchGroupFromMDNList(Collection<String> mdnList, int
            corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubsNotInDispatchGroupFromMDNList(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        Collection<String> subsNotInDispatchGrp = null;
        KnXDMCorpGroupDistInfoDAO corpMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupDistInfoDAO(pttServerId);
        subsNotInDispatchGrp = corpMemberListDAO.getSubsNotInDispatchGroupFromMDNList(mdnList, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return subsNotInDispatchGrp;
    }

    public Collection<String> getSubsNotInNormalDispatchGroupFromMDNList(Collection<String> mdnList, int
            corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubsNotInNormalDispatchGroupFromMDNList(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        Collection<String> subsNotInDispatchGrp = null;
        KnXDMCorpGroupDistInfoDAO corpMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupDistInfoDAO(pttServerId);
        subsNotInDispatchGrp = corpMemberListDAO.getSubsNotInNormalDispatchGroupFromMDNList(mdnList, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return subsNotInDispatchGrp;
    }

    public Collection<String> getLocWatcherMdn(Collection<String> mdnList, int
            corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getLocWatcherMdn(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        Collection<String> subsNotInDispatchGrp = null;
        KnXDMCorpGroupDistInfoDAO corpMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupDistInfoDAO(pttServerId);
        subsNotInDispatchGrp = corpMemberListDAO.getLocWatcherMdn(mdnList, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return subsNotInDispatchGrp;
    }

    public Map<Integer, String> getCorpIdListWhereIsExternalContact(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpIdListWhereIsExternalContact(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        Map<Integer, String> corpIdExtContactNameMap;
        KnXDMExtPoCSubscriberDAO extSubscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMExtPoCSubscriberDAO(pttServerId);
        //retrieve subsc info
        corpIdExtContactNameMap = extSubscDAO.getCorpIdListWhereIsExternalContact(mdn, persisterTxn);
        return corpIdExtContactNameMap;
    }

    public Map<Integer, String> getCorpIdListWhereIsExternalContact(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpIdListWhereIsExternalContact(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        Map<Integer, String> corpIdExtContactNameMap;
        KnXDMExtPoCSubscriberDAO extSubscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMExtPoCSubscriberDAO(pttServerId);
        //retrieve subsc info
        corpIdExtContactNameMap = extSubscDAO.getCorpIdListWhereIsExternalContact(mdnList, persisterTxn);
        return corpIdExtContactNameMap;
    }

    public Map<Integer, KnCorpSubscriberDTO> getCorpIdContactTypeWhereIsExternalContact(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpIdContactTypeWhereIsExternalContact(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        Map<Integer, KnCorpSubscriberDTO> corpIdExtContactNameMap;
        KnXDMExtPoCSubscriberDAO extSubscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMExtPoCSubscriberDAO(pttServerId);
        //retrieve subsc info
        corpIdExtContactNameMap = extSubscDAO.getCorpIdContactTypeWhereIsExternalContact(mdn, persisterTxn);
        return corpIdExtContactNameMap;
    }

    public int getCorpGroupCount(int corpId, List<Integer> groupTypeList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpGroupCount(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        //retrieve subsc info
        return groupInfoDAO.getCorpGroupCount(corpId, groupTypeList, persisterTxn);

    }

    public Map<String, KnIPCorpActivationDTO> getActivationCodeForCorpoateSubscriber(Collection<String> mdnList, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        return getActivationCodeForCorpoateSubscriber(mdnList, false, persisterTxn);
    }

    public Map<String, KnIPCorpActivationDTO> getActivationCodeForCorpoateSubscriber(Collection<String> mdnList, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        KnXDMCorpActivationDAO activationDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpActivationDAO(pttServerId);
        return activationDAO.getActivationCodeForCorpoateSubscriber(mdnList, readOnly, persisterTxn);
    }


    public boolean isSubscriberPartOfGroup(int groupId, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isSubscriberPartOfGroup(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupDistInfoDAO groupDistDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupDistInfoDAO(pttServerId);
        return groupDistDAO.isSubscriberPartOfGroup(groupId, mdn, persisterTxn);
    }

    public Map<String, KnCorpGroupMemberDTO> getGroupMemberDetailsListInfo(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isSubscriberPartOfGroup(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupMemberListDAO grpMemListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return grpMemListDAO.getGroupMemberDetailsListInfo(groupId, persisterTxn);
    }

    public Map<Integer, Collection<String>> getAllSubscribersGroupDistForAllCorporate(Collection<String> mdnList,
                                                                                      KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getAllSubscribersGroupDistForAllCorporate(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        Map<Integer, Collection<String>> groupMemberMap = null;
        KnXDMCorpGroupDistInfoDAO corpMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupDistInfoDAO(pttServerId);
        groupMemberMap = corpMemberListDAO.getAllSubscribersGroupDistForAllCorporate(mdnList, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return groupMemberMap;
    }


    public void updateCorporateEtagForIdList(Collection<Integer> corpIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateCorporateEtagForIdList(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpInfoDAO corpInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpInfoDAO(pttServerId);
        corpInfoDAO.updateCorporateEtagForIdList(corpIdList, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
    }

    public Map<String, Collection<KnCorpSubscriberDTO>> getSubscribersContactDeatilsList(Collection<String> completeMdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscribersContactDeatilsList(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");

        KnXDMCorpContactListDAO corpListMemDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpContactListDAO(pttServerId);
        Map<String, Collection<KnCorpSubscriberDTO>> contactList = corpListMemDAO.getSubscribersContactDeatilsList(completeMdnList, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return contactList;
    }

    public Map<Integer, Collection<String>> getGroupSpecificSubsc(Collection<Integer> groupIds,
                                                                  int supervisorType,
                                                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupSpecificSubsc(Collection<Integer>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");

        KnXDMCorpGroupMemberListDAO corpGroupMemList = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        Map<Integer, Collection<String>> groupMemList = corpGroupMemList.getGroupSpecificSubsc(groupIds, supervisorType, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return groupMemList;
    }

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getCoporateGrpSpecificSubsc(Collection<Integer> supervisorTypes, int
            corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO corpGroupMemList = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return corpGroupMemList.getCoporateGrpSpecificSubsc(supervisorTypes,
                corpId, readOnly, persisterTxn);
    }

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getGrpSpecificSubsc(Collection<Integer> supervisorTypes, Collection<Integer>
            groupIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO corpGroupMemList = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return corpGroupMemList.getGrpSpecificSubsc(supervisorTypes, groupIds, readOnly, persisterTxn);
    }

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getGroupListForGroupMDN(Collection<String> groupMDNList, int
            corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO corpGroupMemList = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return corpGroupMemList.getGroupListForGroupMDN(groupMDNList, corpId, persisterTxn);
    }


    public Map<String, String> getSubscribersName(Set<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO corpGroupMemList = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return corpGroupMemList.getSubscribersName(mdnList, readOnly, persisterTxn);
    }

    /**
     * This method is used to get corporates where the subscriber is an external contact
     *
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<Integer> getExtCorpForSub(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getExtCorpForSub(String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMExtPoCSubscriberDAO extPoCSubscriberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMExtPoCSubscriberDAO(pttServerId);
        return extPoCSubscriberDAO.getExtCorpForSub(mdn, persisterTxn);
    }

    /**
     * This method returns the external member list in a group.
     *
     * @param groupId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public LinkedList<String> getExternalGrpMembers(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getExternalGrpMembers(int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :", groupId);
        KnXDMCorpGroupMemberListDAO corpGroupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return corpGroupMemberListDAO.getExternalGrpMembers(groupId, persisterTxn);
    }

    public void updateSublistEtag(int sublistId, long etag, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateSublistEtag(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListInfoDAO subscInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        subscInfoDAO.updateSublistEtag(sublistId, etag, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
    }

    public Collection<String> getDeActNonHandsetSubsc(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getDeActNonHandsetSubsc(corpId, persisterTxn) ";
        knLogger.debug(methodName, "ENTRY : ");
        Collection<String> mdnList;
        KnXDMSubscriberInfoDAO subscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        mdnList = subscInfoDAO.getDeActNonHandsetSubsc(corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return mdnList;
    }

    public Collection<KnCorpSubscriberDTO> getSubscribersInfo(Collection<String> addedMdnList,
                                                              KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberInfo(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : addedMdnList ~ ", addedMdnList == null ? addedMdnList : KnGDPRTemplate.mdnList(addedMdnList));
        Connection conn;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        String query = null;
        int index=1;
        Collection<KnCorpSubscriberDTO> subscribersList = new ArrayList<KnCorpSubscriberDTO>();
        boolean ownedTxn = false;
        try {
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                knLogger.debug(methodName, "opening the transaction");
                persisterTxn.open();
                ownedTxn = true;
            }
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            if (addedMdnList != null && !addedMdnList.isEmpty()) {
                query = queryMapper.getQuery(GET_POC_SUBSCRIBER_INFO);
                query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedQuesMarks(addedMdnList));
                knLogger.debug(methodName, "query", query);
                pStmt = conn.prepareStatement(query);
                for(String mdn:addedMdnList){
                    pStmt.setString(index++, mdn);
                }
                rs = pStmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully.");
                while (rs.next()) {
                    KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                    String mdn = rs.getString(2).trim();
                    subscriberDTO.setMdn(mdn);
                    int mdnCorpId = rs.getInt(1);
                    subscriberDTO.setCorpId(mdnCorpId);
                    //multilingual revert changes
                    if (rs.getString(3) != null) {
                        subscriberDTO.setName(new String(rs.getString(3).getBytes("8859_1"), "UTF-8"));
                    }
                    subscriberDTO.setClientType(rs.getInt(4));
                    subscriberDTO.setUserAgent(rs.getString(5));
                    subscriberDTO.setClientPVmajorVer(rs.getInt(6));
                    subscriberDTO.setAliasMdn(rs.getString(7));
                    subscriberDTO.setUserId(rs.getString(8));
                    subscriberDTO.setMcpttCompliance(rs.getInt(11));
                    String activefs1 = KnGeneralUtil.convertLongToHexString(rs.getLong(9));
                    subscriberDTO.setSubsActiveFS1(activefs1);
                    String activeFs=rs.getString(10)!=null?rs.getString(10):KnGeneralUtil.convertLongToHexString(rs.getLong(9));
                    subscriberDTO.setSubsActiveFS2(activeFs);
                    String subsFs2=rs.getString(12)!=null?rs.getString(12):KnGeneralUtil.convertLongToHexString(rs.getLong(13));
                    subscriberDTO.setSubscriberFs2(subsFs2);
                    String corpAdminFs1 = KnGeneralUtil.convertLongToHexString(rs.getLong(14));
                    subscriberDTO.setCorpAdminFS1(corpAdminFs1);
                    String corpAdminFs2=rs.getString(15)!=null?rs.getString(15):KnGeneralUtil.convertLongToHexString(rs.getLong(14));
                    subscriberDTO.setCorpAdminFS2(corpAdminFs2);
                    subscribersList.add(subscriberDTO);
                }
            }
            knLogger.debug(methodName, "EXIT :", "addedMdnList ~ ", KnGDPRTemplate.mdnList(addedMdnList), "Fetched the details from DB :  ", subscribersList.size());
            if(ownedTxn){
                knLogger.debug(methodName, "saving the transaction ");
                persisterTxn.save();
            }
            return subscribersList;

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the distinct members from DB - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while fetching the distinct members from DB- ",
                    e);
            if(ownedTxn) {
                persisterTxn.rollback();
            }
            throw KnDbUtil.processException(e, "Failed while fetching the distinct members from DB" + e,
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pStmt);
        }
    }

    public Map<String, KnCorpSubscriberDTO> getSubscIsMemOfDispGrpDetails(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscIsMemOfDispGrpDetails(mdnList, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        KnXDMSubscriberInfoDAO subscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscInfoDAO.getSubscIsMemOfDispGrpDetails(mdnList, persisterTxn);
    }

    public Map<String, Collection<Integer>> getGroupListForSubs(Collection<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getGroupListForSubs(Collection<String>,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpGroupDistInfoDAO groupDistInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupDistInfoDAO(pttServerId);
        //retrieve subsc info
        Map<String, Collection<Integer>> subscDistInfo = groupDistInfoDAO.getGroupListForSubs(mdnList, readOnly, persisterTxn);
        return subscDistInfo;
    }

    public Map<Integer, Integer> getGroupListForSubsWithCorpId(Collection<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getGroupListForSubsWithCorpId(Collection<String>,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point : ");
        KnXDMCorpGroupMemberListDAO groupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMCorpGroupMemberListDAO(pttServerId);
        return groupMemberListDAO.getGroupListForSubsWithCorpId(mdnList, readOnly, persisterTxn);
    }

    public Collection<String> getPocSubscribersDetails(Collection<String> contactMDNs, int corpId,
                                                       KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getPocSubscribersDetails(Collection<String>,int, KnPersisterTxn) ";
        Collection<String> subsList = new ArrayList<>();
        if (contactMDNs == null || contactMDNs.isEmpty()) {
            knLogger.info(methodName, "ENTRY: requested mdn is empty");
            return subsList;
        }
        knLogger.info(methodName, "ENTRY: requested mdn size - ", contactMDNs.size());
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        var splittedContactMdnsList = KnGeneralUtil.splitList(new ArrayList<>(contactMDNs), BULK_UPDATE_SIZE);
        for (var eachSplittedContactMdns : splittedContactMdnsList) {
            var eachSplittedSubsList = subscriberTable.getPocSubscribersDetails(eachSplittedContactMdns, corpId, persisterTxn);
            if (eachSplittedSubsList != null && !eachSplittedSubsList.isEmpty()) {
                subsList.addAll(eachSplittedSubsList);
            }
        }
        knLogger.info(methodName, "EXIT Point. subsList size- ", subsList.size());
        return subsList;
    }

    public Map<String,Integer> getMdnCorpIdMapping(Collection<String> contactMDNs,
                                                       KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getMdnCorpIdMapping(Collection<String>,int, KnPersisterTxn) ";
        Map<String,Integer> mdnCorpIdMapping = new HashMap<>();
        if (contactMDNs == null || contactMDNs.isEmpty()) {
            knLogger.info(methodName, "ENTRY: requested mdn is empty");
            return mdnCorpIdMapping;
        }
        knLogger.info(methodName, "ENTRY: requested mdn size - ", contactMDNs.size());
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        var splittedContactMdnsList = KnGeneralUtil.splitList(new ArrayList<>(contactMDNs), BULK_UPDATE_SIZE);
        for (var eachSplittedContactMdns : splittedContactMdnsList) {
            var eachSplittedSubsList = subscriberTable.getMdnCorpIdMapping(eachSplittedContactMdns, persisterTxn);
            if (eachSplittedSubsList != null && !eachSplittedSubsList.isEmpty()) {
                mdnCorpIdMapping.putAll(eachSplittedSubsList);
            }
        }
        knLogger.info(methodName, "EXIT Point. mdnCorpidMapping size- ", mdnCorpIdMapping.size());
        return mdnCorpIdMapping;
    }


    public Collection<KnCorpSubscriberDTO> getSubscriberProfileInfo(Collection<String> mdnList, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscriberProfileInfo(Collection<String>,int, boolean, KnPersisterTxn) ";
        knLogger.debug(methodName, "ENTRY: ");
        Collection<KnCorpSubscriberDTO> subsList;
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        subsList = subscriberTable.getSubscriberProfileInfo(mdnList, corpId, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return subsList;
    }


    public Collection<KnCorpSubscriberDTO> getSubscriberProfileDetails(Collection<String> mdnList, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscriberProfileDetails(Collection<String>,int,boolean, KnPersisterTxn) ";
        knLogger.debug(methodName, "ENTRY: ");
        Collection<KnCorpSubscriberDTO> subsList;
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        subsList = subscriberTable.getSubscriberProfileDetails(mdnList, corpId, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return subsList;
    }

    /**
     * This method returns the list of group ids where subscriber exist as member.
     *
     * @param subsMdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<Integer> getSubsGroupIdList(String subsMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupDistInfoDAO groupDistInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupDistInfoDAO(pttServerId);
        return groupDistInfoDAO.getSubsGroupIdList(subsMdn, persisterTxn);
    }

    /**
     * This method returns the group ids where the subscriber exist
     *
     * @param subsGroupIdList
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, KnCorpGrpBasicInfoDTO> getGrpDetForGetDir(List<Integer> subsGroupIdList, int corpId, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getGrpDetForGetDir(subsGroupIdList, corpId, persisterTxn);
    }

    /**
     * This method returns a Map of subscribers and subscriber name from dg.pocsubscrinfo table.
     *
     * @param mdnList
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, KnSubscriberDTO> getSubscribersNameForCorp(List<String> mdnList, int corpId,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberInfoDAO.getSubscribersNameForCorp(mdnList, corpId,readOnly, persisterTxn);
    }

    /**
     * This method returns a Map of subscriber MDN and subscriber DTO from dg.pocsubscrinfo table.
     *
     * @param mdnList
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, KnCorpSubscriberDTO> getSubscriberDto(List<String> mdnList, int corpId, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberInfoDAO.getSubscriberDto(mdnList, corpId, readOnly, persisterTxn);
    }

    public Collection<KnCorpSubscriberDTO> getExternalPoCSubscriberInfo(List<String> contactListDTO, int
            corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        return getExternalPoCSubscriberInfo(contactListDTO, corpId, false, persisterTxn);
    }

    /**
     * This method query dg.extcorpxontact table and return the external contact details in the corporation. This is read only method.
     *
     * @param contactListDTO
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<KnCorpSubscriberDTO> getExternalPoCSubscriberInfo(List<String> contactListDTO, int
            corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMExtPoCSubscriberDAO extPoCSubsDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(pttServerId);
        return extPoCSubsDAO.selectExternalPoCSubscriberInfo(contactListDTO, corpId, readOnly, persisterTxn);
    }

    public Map<String, KnCorpGroupMemberDTO> getGroupMembersList(int groupId,boolean readonly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO grpMemListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return grpMemListDAO.getGroupMembersList(groupId,readonly, persisterTxn);
    }

    public Collection<KnCorpGroupMemberDTO> getCorpGroupMembersList(Collection<Integer> groupIds,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO grpMemListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return grpMemListDAO.getCorpGroupMembersList(groupIds,readOnly, persisterTxn);
    }

    public Collection<KnCorpGroupMemberDTO> getCorpGroupMemberForLocWatcher(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO grpMemListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return grpMemListDAO.getCorpGroupMemberForLocWatcher(groupIds, persisterTxn);
    }

    public Collection<String> getCorpGroupMemberIsLocWatcher(Collection<Integer> groupIds, int isLocwatcher, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO grpMemListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return grpMemListDAO.getCorpGroupMemberIsLocWatcher(groupIds, isLocwatcher, persisterTxn);
    }

    public Map<Integer, List<String>> getCorpGroupMemberForLocSupervisor(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO grpMemListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return grpMemListDAO.getCorpGroupMemberForLocSupervisor(groupIds, persisterTxn);
    }

    /**
     * This method calls a SP to get the truncated group members
     *
     * @param groupId
     * @param maxGroupMemberLimit
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<String> getTruncatedGroupMems(int groupId, int maxGroupMemberLimit, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        return groupMemberListDAO.getTruncatedGroupMems(groupId, maxGroupMemberLimit, readOnly, persisterTxn);
    }

    public Map<String, KnSubscriberDTO> getExtContName(List<String> contactListDTO, int corpId,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMExtPoCSubscriberDAO extPoCSubsDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(pttServerId);
        return extPoCSubsDAO.getExtContName(contactListDTO, corpId, readOnly, persisterTxn);
    }

    /**
     * This method returns the Map of mdn and contact count.
     *
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, Integer> getSubsContactCount(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMCorpContactCountDAO corpContactCount =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactCountDAO(pttServerId);
        return corpContactCount.getSubsContactCount(mdnList, readOnly, persisterTxn);
    }

    public Map<String, Integer> getGroupPrivtMemLst(int groupId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListMemberDAO corpListMemDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        return corpListMemDAO.getGroupPrivtMemLst(groupId, readOnly, persisterTxn);
    }

    public Collection<KnCorpGroupMemPersistDTO> getGrpMemDetails(List<String> internalMemLst, Map<String, Integer>
            contCountmap, int corpId, int maxContact, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberInfoDAO.getGrpMemDetails(internalMemLst, contCountmap, corpId, maxContact, readOnly, persisterTxn);
    }

    public List<String> getGrpMemDetails(List<String> internalMemLst, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberInfoDAO.getGrpMemDetails(internalMemLst, persisterTxn);
    }

    public List<String> getMdnsLessThanThirteenPv(List<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberInfoDAO.getMdnsLessThanThirteenPv(mdns, persisterTxn);
    }

    /**
     * This method query the dg.corpextcontact table and returns the List of  KnCorpGroupMemPersistDTO
     *
     * @param extMemLst
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Collection<KnCorpGroupMemPersistDTO> getExtGrpMemDetails(List<String> extMemLst, int corpId, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        KnXDMExtPoCSubscriberDAO extPoCSubscriberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMExtPoCSubscriberDAO(pttServerId);
        return extPoCSubscriberDAO.getExtGrpMemDetails(extMemLst, corpId, readOnly, persisterTxn);
    }

    /**
     * This method queries the dg.corplistmember table and returns Map of collections of Internal and external members
     *
     * @param sublistId
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, List<String>> getSublistMemMap(int sublistId, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListMemberDAO corpListMemDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        return corpListMemDAO.getSublistMemMap(sublistId, corpId, readOnly, persisterTxn);
    }

    /**
     * This method returns the subscribers details from dg.pocsubscrinfo table.
     *
     * @param operationType
     * @param membersList
     * @param corpId
     * @param maxContactLimit
     * @param contCountMap
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnCorpContactDTO> getSubsDetails(String operationType, List<String> membersList, int corpId, int
            maxContactLimit, Map<String, Integer> contCountMap, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscInfoDAO.getSubsDetails( operationType, membersList, corpId, maxContactLimit, contCountMap, readOnly, persisterTxn);
    }

    /**
     * This method returns the List of external contact details.
     *
     * @param extMemLst
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnCorpContactDTO> getExtSubListMemDetails(List<String> extMemLst, int corpId, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        KnXDMExtPoCSubscriberDAO extPoCSubscriberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMExtPoCSubscriberDAO(pttServerId);
        return extPoCSubscriberDAO.getExtSublistMemDetails(extMemLst, corpId, readOnly, persisterTxn);
    }

    /**
     * This method retrieves subscribres info from subscrinfo table.
     *
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnCorpSubscriberDTO selectPocSubscriberInfo(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.selectPocSubscriberInfo(mdn, readOnly, persisterTxn);
    }

    public Map<Integer, Integer> getValidGrpInCorp(List<Integer> grpIdList, int corpId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        return corpGroupInfoDAO.getValidGrpInCorp(grpIdList, corpId, readOnly, persisterTxn);
    }

    public Map<Integer, Integer> getValidGrp(List<Integer> grpIdList, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        return corpGroupInfoDAO.getValidGrp(grpIdList, persisterTxn);
    }

    /**
     * This method filters out the groupId from a list of groupids where mdn doest not exist as member and return the subset
     * of groupIds from input list where mdn exist as member.
     *
     * @param grpList
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<Integer> getSubsGroupIdList(List<Integer> grpList, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupDistInfoDAO groupDistInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupDistInfoDAO(pttServerId);
        return groupDistInfoDAO.getSubsGroupIdList(grpList, mdn, persisterTxn);
    }

    /**
     * This method query the DG.POCSUBSCRINFO and DG.CORPGROUPDISTINFO tables and return all subscribers in the
     * PAM Account ID without groups.
     *
     * @param pamAcId
     * @param clientType
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<String> getCorpSubsWithNoGrps(int pamAcId, int clientType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpSubsWithNoGrps(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : pamAcId - ", pamAcId, ", clientType - " + clientType);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<String> mdnListNoGrps = new ArrayList<String>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(SELECT_CORP_MDN_WITHOUT_GROUPS);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, pamAcId);
            pstmt.setInt(2, clientType);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                mdnListNoGrps.add(rs.getString(1).trim());
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetching the distinct members from DB",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, "Subscribers without groups size - ", mdnListNoGrps.size());
        return mdnListNoGrps;
    }

    /**
     * This method query the DG.POCSUBSCRINFO and DG.CORPLISTDISTINFO tables and return all subscribers in the
     * PAM Account ID who does not have contacts.
     *
     * @param pamAcId
     * @param clientType
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<String> getCorpSubsWithNoConts(int pamAcId, int clientType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpSubsWithNoConts(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : pamAcId - ", pamAcId, ", clientType - " + clientType);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<String> mdnListNoGrps = new ArrayList<String>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = queryMapper.getQuery(SELECT_CORP_MDN_WITHOUT_CONTACTS);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, pamAcId);
            pstmt.setInt(2, clientType);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                mdnListNoGrps.add(rs.getString(1).trim());
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetching the distinct members from DB",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.debug(methodName, "Subscribers without contacts - ", mdnListNoGrps.size());
        return mdnListNoGrps;
    }

    /**
     * Method to return a Map of MDN who has deleting MDN as contact with the deleting contact list.
     *
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, List<String>> getSubsContactList(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpContactListDAO contactListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpContactListDAO(pttServerId);
        return contactListDAO.getSubsContactList(mdnList, persisterTxn);
    }

    /**
     * This method returns a Map of sublist id and members in mdnList
     *
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, List<String>> getSubsSublistList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListMemberDAO corpListMemberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        return corpListMemberDAO.getSubsSublistList(mdnList, persisterTxn);
    }

    /**
     * This method returns a Map which contains groupId and the list of deleted members present in the groupId.
     *
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<Integer, List<KnCorpGrpMemListDTO>> getSubsGroupIdListMap(List<String> mdnList, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return groupMemberListDAO.getSubsGroupIdListMap(mdnList, persisterTxn);
    }

    /**
     * This method deleted the group members in mdnList from dg.corpgroupmemberlist table.
     *
     * @param mdnList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteGrpMemList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        groupMemberListDAO.deleteGrpMemList(mdnList, persisterTxn);
    }

    /**
     * This method deleted the corp group distribution entries for mdnList.
     *
     * @param mdnList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteGrpDistList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupDistInfoDAO groupDistInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupDistInfoDAO(pttServerId);
        groupDistInfoDAO.deleteGrpDistList(mdnList, persisterTxn);
    }

    /**
     * This method deleted the Group_Sublist entries for sublistId.
     *
     * @param sublistId
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteCorpListDistGroupReference(List<Integer> sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupListRefDAO corpGroupListRefDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupListRefDAO(pttServerId);
        corpGroupListRefDAO.deleteCorpListDistGroupReference(sublistId, persisterTxn);

    }

    /**
     * This method deleted the contactlist entries for mdnList.
     *
     * @param mdnList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteCorpContactList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpContactListDAO contactListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpContactListDAO(pttServerId);
        contactListDAO.deleteCorpContactList(mdnList, persisterTxn);
    }

    /**
     * This method deleted the sublist members in mdnList from dg.corplistmember table.
     *
     * @param mdnList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteCorpSublistMemList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListMemberDAO corpListMemberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        corpListMemberDAO.deleteCorpSublistMemList(mdnList, persisterTxn);
    }

    /**
     * This methods returns the empty sublists fromsublistIds.
     *
     * @param sublistIds
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<Integer> getEmptySublistIds(List<Integer> sublistIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListMemberDAO corpListMemberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        return corpListMemberDAO.getEmptySublistIds(sublistIds, persisterTxn);
    }

    /**
     * This method deleted the Sublist distribution entries for sublistIds and mdnList.
     *
     * @param sublistIds
     * @param mdnList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteCorpListDistribution(List<Integer> sublistIds, List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListDistInfoDAO listDistInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListDistInfoDAO(pttServerId);
        listDistInfoDAO.deleteCorpListDistribution(sublistIds, persisterTxn);
        listDistInfoDAO.deleteCorpListDistForMdn(mdnList, persisterTxn);
    }

    /**
     * Deletes all CORPLISTDISTINFO entries where the given MDNs are recipients.
     * Used during unAllocateSubs to remove stale sublist distribution for an MDN leaving a hierarchy.
     */
    public void deleteCorpListDistForMdn(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListDistInfoDAO listDistInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListDistInfoDAO(pttServerId);
        listDistInfoDAO.deleteCorpListDistForMdn(mdnList, persisterTxn);
    }

    /**
     * This method deleted sublist entries from corplistinfo table
     *
     * @param sublistIds
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteSublistInfoList(List<Integer> sublistIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListInfoDAO corpListInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        corpListInfoDAO.deleteSublistInfoList(sublistIds, persisterTxn);
    }

    /**
     * This method deletes sublists entries from corplistinfo table using corpId
     * @param corpId
     * @param persisterTxn
     * @throws KnDAOException
     */
    @Override
    public void deleteAllSubListsOfCorporate(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListInfoDAO corpListInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        corpListInfoDAO.deleteAllSubListsOfCorporate(corpId,persisterTxn);
    }
    /**
     * This method deleted excernal contact entries from extcorpcontact table.
     *
     * @param mdnList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteSusbcribersFromExtContactTables(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMExtPoCSubscriberDAO extSubscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMExtPoCSubscriberDAO(pttServerId);
        extSubscDAO.deleteSusbcribersFromExtContactTables(mdnList, persisterTxn);
    }

    /**
     * This method returns list od corporate Id where menList exist as external contact
     *
     * @param mdnList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<Integer> getCorpIdList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMExtPoCSubscriberDAO extSubscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMExtPoCSubscriberDAO(pttServerId);
        return extSubscDAO.getCorpIdList(mdnList, persisterTxn);
    }


    public boolean cleanUpSubsCampedGrps(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        boolean modeChange = false;
        KnXDMCorpCampGrpDOA xdmCorpCampGrpDOA = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCampedGrpDAO(pttServerId);
        xdmCorpCampGrpDOA.cleanUpSubsCampedGrps(mdn, persisterTxn);
        KnXDMCorpChannelGrpDAO xdmChannelGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMChannelGrpDAO(pttServerId);
        xdmChannelGrpDAO.cleanUpSubsChannelGrps(mdn, persisterTxn);
        int rowDeleted = xdmCorpCampGrpDOA.deleteSubsTalkGrpScanMode(mdn, persisterTxn);
        if (rowDeleted > 0) {
            modeChange = true;
        }
        return modeChange;
    }

    public boolean cleanUpSubsCampedGrps(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        boolean modeChange = false;
        KnXDMCorpCampGrpDOA xdmCorpCampGrpDOA = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCampedGrpDAO(pttServerId);
        xdmCorpCampGrpDOA.cleanUpSubsCampedGrps(mdnList, persisterTxn);
        KnXDMCorpChannelGrpDAO xdmChannelGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMChannelGrpDAO(pttServerId);
        xdmChannelGrpDAO.cleanUpSubsChannelGrps(mdnList, persisterTxn);
        int rowDeleted = xdmCorpCampGrpDOA.deleteSubsTalkGrpScanMode(mdnList, persisterTxn);
        if (rowDeleted > 0) {
            modeChange = true;
        }
        return modeChange;
    }

    public void deleteSubsCampedGrps(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpCampGrpDOA xdmCorpCampGrpDOA = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCampedGrpDAO(pttServerId);
        xdmCorpCampGrpDOA.cleanUpSubsCampedGrps(mdn, persisterTxn);
    }

    public void deleteSubsChannelGrps(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpChannelGrpDAO xdmChannelGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMChannelGrpDAO(pttServerId);
        xdmChannelGrpDAO.cleanUpSubsChannelGrps(mdn, persisterTxn);
    }

    public void deleteCampedGrps(String mdn, List<Integer> grpIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpCampGrpDOA xdmCorpCampGrpDOA = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCampedGrpDAO(pttServerId);
        xdmCorpCampGrpDOA.deleteCampedGrps(mdn, grpIdList, persisterTxn);
    }

    public void createSubsCampedGrps(String mdn, List<KnCorpTalkGrpInfoDTO> grpList, int campedBy, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpCampGrpDOA xdmCorpCampGrpDOA = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCampedGrpDAO(pttServerId);
        xdmCorpCampGrpDOA.createSubsCampedGrps(mdn, grpList, campedBy, persisterTxn);
    }

    public void createSubsChannelGrps(String mdn, List<KnCorpTalkGrpInfoDTO> grpList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpChannelGrpDAO xdmChannelGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMChannelGrpDAO(pttServerId);
        xdmChannelGrpDAO.createSubsChannelGrps(mdn, grpList, persisterTxn);
    }

    public void createSubsTalkGrpScanMode(String mdn, Integer mode, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpCampGrpDOA xdmCorpCampGrpDOA = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCampedGrpDAO(pttServerId);
        xdmCorpCampGrpDOA.createSubsTalkGrpScanMode(mdn, mode, persisterTxn);
    }

    public void deleteSubsTalkGrpScanMode(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpCampGrpDOA xdmCorpCampGrpDOA = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCampedGrpDAO(pttServerId);
        xdmCorpCampGrpDOA.deleteSubsTalkGrpScanMode(mdn, persisterTxn);
    }

    public List<KnCorpTGSPersistDTO> getSubsCampedGrp(KnIPTalkGroupDTO ipTalkGroupDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpCampGrpDOA xdmCorpCampGrpDOA = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCampedGrpDAO(pttServerId);
        return xdmCorpCampGrpDOA.getSubsCampedGrp(ipTalkGroupDTO, readOnly, persisterTxn);
    }

    public Map<Integer, KnCorpGroupInfoPersistDTO> getGroupDisplayNameCorpIdMapInfo(List<Integer> groupIdLst, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpCampGrpDOA xdmCorpCampGrpDOA = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCampedGrpDAO(pttServerId);
        return xdmCorpCampGrpDOA.getGroupDisplayNameCorpIdMapInfo(groupIdLst, readOnly, persisterTxn);
    }

    public void deleteAllCampedGroups(KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpCampGrpDOA xdmCorpCampGrpDOA = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCampedGrpDAO(pttServerId);
        xdmCorpCampGrpDOA.deleteAllCampedGroups(persisterTxn);
    }

    public void modifySubsCorpFeatureSet(KnCorpSubscriberDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO xdmSubscriberInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        xdmSubscriberInfoDAO.modifySubsCorpFeatureSet(corpSubscriberDTO, persisterTxn);
    }

    @Override
    public void updateSubsTalkGrpScanMode(String mdn, Integer mode, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpCampGrpDOA xdmCorpCampGrpDOA = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCampedGrpDAO(pttServerId);
        xdmCorpCampGrpDOA.updateSubsTalkGrpScanMode(mdn, mode, persisterTxn);
    }

    @Override
    public void updateSubsTalkGrpScanEtag(String mdn, Integer etag, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpCampGrpDOA xdmCorpCampGrpDOA = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCampedGrpDAO(pttServerId);
        xdmCorpCampGrpDOA.updateSubsTalkGrpScanEtag(mdn, etag, persisterTxn);
    }

    @Override
    public KnTalkGrpScanMode getSubsTalkGrpScanMode(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpCampGrpDOA xdmCorpCampGrpDOA = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCampedGrpDAO(pttServerId);
        return xdmCorpCampGrpDOA.getSubsTalkGrpScanMode(mdn, readOnly, persisterTxn);
    }

    @Override
    public KnCorpSubscriberDTO getSubscriberDetail(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscriberProfileDetails(Collection<String>,int, KnPersisterTxn) ";
        knLogger.debug(methodName, "ENTRY: ");
        KnCorpSubscriberDTO subsList;
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        subsList = subscriberTable.getSubscriberDetail(mdn, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return subsList;
    }

    @Override
    public KnCorpInOutParamDTO cleanUpCampedGrp(Map<Integer, List<String>> map, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "cleanUpCampedGrp(Map<Integer, List<String>> map, Integer grpId, KnPersisterTxn persisterTxn)";
        KnCorpInOutParamDTO corpIODTO = new KnCorpInOutParamDTO();
        Map<String, KnTGSModeChgDTO> tgsModeChgMap = new HashMap<String, KnTGSModeChgDTO>();
        knLogger.debug(methodName, "map", map);
        KnXDMCorpCampGrpDOA xdmCorpCampGrpDOA = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCampedGrpDAO(pttServerId);
        KnXDMCorpChannelGrpDAO xdmCorpChannelGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMChannelGrpDAO(pttServerId);
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        Set<String> uniqueSet = new HashSet<String>();
        List<String> flatList = new ArrayList<String>();
        for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
            List<String> mdns = entry.getValue();
            flatList.addAll(mdns);
        }
        //get the list of subscribers campped to this group and form it as the unique list  from the check that its passed in the map data
        List<Integer> grpIdList = new ArrayList<Integer>(map.keySet());
        for (Integer gId : grpIdList) {
            List<String> mdns = xdmCorpCampGrpDOA.getMdnsForCampedGrp(gId, persisterTxn);
            List<String> channelMdns = xdmCorpChannelGrpDAO.getMdnsForChannelGrp(gId, persisterTxn);

            for (String mdn : mdns) {
                if (flatList.contains(mdn)) {
                    uniqueSet.add(mdn);
                }
            }
            for (String mdn : channelMdns) {
                if (flatList.contains(mdn)) {
                    uniqueSet.add(mdn);
                }
            }
            //uniqueSet.addAll(mdns);
        }
        xdmCorpCampGrpDOA.deleteCampedGrps(map, persisterTxn);
        xdmCorpChannelGrpDAO.deleteChannelGrps(map, persisterTxn);

        List<String> mdnList = new ArrayList<String>(uniqueSet);
        Map<String, Integer> subscCampGrpList = xdmCorpCampGrpDOA.getCampedGrpCount(mdnList, persisterTxn);
        Map<String, Integer> subscChannelGrpList = xdmCorpChannelGrpDAO.getChannelGrpCount(mdnList, persisterTxn);

        //get the etag for the tgsc for the subscribers still having some groups that are being scanned.
        Map<String, Integer> mdnTGSCEtag = xdmCorpCampGrpDOA.getTGSCDocumentEtag(mdnList, persisterTxn);
        for (String mdn : uniqueSet) {
            //  int campedGrpCount = xdmCorpCampGrpDOA.getCampedGrpCount(mdn, persisterTxn);

            if (subscCampGrpList.get(mdn) == 0 && subscChannelGrpList.get(mdn) == 0) {
                xdmCorpCampGrpDOA.deleteSubsTalkGrpScanMode(mdn, persisterTxn);
                KnCorpSubscriberDTO corpSubscriberDTO = subscriberTable.getSubscriberDetail(mdn, persisterTxn);
                KnTGSModeChgDTO tgsModeChgDTO = new KnTGSModeChgDTO();
                tgsModeChgDTO.setPocHome(corpSubscriberDTO.getPocHome());
                tgsModeChgDTO.setPresenceHome(corpSubscriberDTO.getPresenceHome());
                tgsModeChgDTO.setTgsMode(0);
                tgsModeChgMap.put(mdn, tgsModeChgDTO);
            } else {
                Integer etag = mdnTGSCEtag.get(mdn);
                if (etag != null) {
                    knLogger.debug(methodName, "Updating etag tgsc value ", etag);
                    updateSubsTalkGrpScanEtag(mdn, ++etag, persisterTxn);
                    mdnTGSCEtag.put(mdn, etag);
                }
            }
        }

        corpIODTO.putTGSModeChg(tgsModeChgMap);
        corpIODTO.setMdnTgscEtag(mdnTGSCEtag);
        return corpIODTO;
    }

    @Override
    public KnCorpInOutParamDTO cleanUpCampedGrp(List<Integer> deletedGrpIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "cleanUpCampedGrp(Integer deletedGrpId, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "deletedGrpIds", deletedGrpIds);
        Map<String, KnTGSModeChgDTO> tgsModeChgMap = new HashMap<String, KnTGSModeChgDTO>();
        Map<String, List<Integer>> groupMdnMap = new HashMap<String, List<Integer>>();
        KnCorpInOutParamDTO corpIODTO = new KnCorpInOutParamDTO();
        KnXDMCorpCampGrpDOA xdmCorpCampGrpDOA = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCampedGrpDAO(pttServerId);
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        KnXDMCorpChannelGrpDAO xdmCorpChannelGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMChannelGrpDAO(pttServerId);

        Set<String> uniqueSet = new HashSet<String>();
        for (Integer deletedGrpId : deletedGrpIds) {
            List<String> mdns = xdmCorpCampGrpDOA.getMdnsForCampedGrp(deletedGrpId, persisterTxn);
            List<String> channelMdns = xdmCorpChannelGrpDAO.getMdnsForChannelGrp(deletedGrpId, persisterTxn);

            uniqueSet.addAll(mdns);
            uniqueSet.addAll(channelMdns);

        }

        List<String> mdns = new ArrayList<String>();
        //get the etag for the tgsc for the subscribers still having some groups that are being scanned.
        Map<String, Integer> mdnTGSCEtag = xdmCorpCampGrpDOA.getTGSCDocumentEtag(new ArrayList<String>(uniqueSet), persisterTxn);

        xdmCorpCampGrpDOA.deleteCampedGrps(null, deletedGrpIds, persisterTxn);
        xdmCorpChannelGrpDAO.deleteChannelGrps(null, deletedGrpIds, persisterTxn);
        Map<String, Integer> campedGrpCountMap = xdmCorpCampGrpDOA.getCampedGrpCount(new ArrayList<String>(uniqueSet), persisterTxn);
        Map<String, Integer> subscChannelGrpList = xdmCorpChannelGrpDAO.getChannelGrpCount(new ArrayList<String>(uniqueSet), persisterTxn);

        //hold the list of subscribers who still have the group being scanned.

        for (String mdn : uniqueSet) {
            mdn = mdn.trim();
            if (campedGrpCountMap.get(mdn) == 0 && subscChannelGrpList.get(mdn) == 0) {
                xdmCorpCampGrpDOA.deleteSubsTalkGrpScanMode(mdn, persisterTxn);
                KnCorpSubscriberDTO corpSubscriberDTO = subscriberTable.getSubscriberDetail(mdn, persisterTxn);
                KnTGSModeChgDTO tgsModeChgDTO = new KnTGSModeChgDTO();
                tgsModeChgDTO.setPocHome(corpSubscriberDTO.getPocHome());
                tgsModeChgDTO.setPresenceHome(corpSubscriberDTO.getPresenceHome());
                tgsModeChgDTO.setTgsMode(0);
                tgsModeChgMap.put(mdn, tgsModeChgDTO);
            } else {
                mdns.add(mdn);
                Integer etag = mdnTGSCEtag.get(mdn);
                if (etag != null) {
                    knLogger.debug(methodName, "Updating etag tgsc value- ", etag);
                    updateSubsTalkGrpScanEtag(mdn, ++etag, persisterTxn);
                    mdnTGSCEtag.put(mdn, etag);
                }
            }
        }
        corpIODTO.putTGSModeChg(tgsModeChgMap);
        corpIODTO.setMdnTgscEtag(mdnTGSCEtag);
        return corpIODTO;
    }

    @Override
    public boolean isExternalSubscriber(String mdn, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "isExternalSubscriber(mdn,corpId,persisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        boolean isExt = false;
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        isExt = subscriberTable.isExternalSubscriber(mdn, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return isExt;
    }

    public boolean ifMdnisSGMDN(String mdn,KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "ifMdnisSGMDN(mdn,persisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.ifMdnisSGMDN(mdn,persisterTxn);
    }

    @Override
    public List<KnDeviceDetailsDTO> getDeviceList(int corpId, Integer filterType, Integer fetchSize, Integer nextToken, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getDeviceList(int corpId, Integer filterType, Integer fetchSize, Integer nextToken, boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnCorpDeviceDAO deviceInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMDeviceDAO(pttServerId);
       return deviceInfoDAO.getDeviceList(corpId, filterType, fetchSize, nextToken, readOnly, persisterTxn);
    }

    @Override
    public Map<String, KnXDMDeviceAddlInfoDTO> getDeviceAddInfoMap(List<String> deviceList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getDeviceAddInfoMap(List<String> deviceList, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnCorpDeviceDAO deviceInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMDeviceDAO(pttServerId);
        return deviceInfoDAO.getDeviceAddInfoMap(deviceList, persisterTxn);
    }

    @Override
    public KnCorpDeviceInfoRespDTO getDeviceDetails(KnIPDeviceInfoDTO deviceInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getDeviceDetails(KnIPDeviceInfoDTO deviceInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnCorpDeviceDAO deviceInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMDeviceDAO(pttServerId);
        return deviceInfoDAO.getDeviceInfo(deviceInfoDTO, readOnly, persisterTxn);
    }

    @Override
    public boolean isDispatchMemberPresent(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "isDispatchMemberPresent(List<String> mdnList, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        boolean isDispatchMemExist = false;
        KnXDMSubscriberInfoDAO xdmSubscriberInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        isDispatchMemExist = xdmSubscriberInfoDAO.isDispatchMemberPresent(mdnList, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return isDispatchMemExist;
    }

    @Override
    public ArrayList<String> getInternalNonDispatchMember(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getInternalNonDispatchMember(Collection<String> mdnList, int corpId KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "mdnList - ", mdnList == null ? mdnList : KnGDPRTemplate.mdnList(mdnList));
        ArrayList<String> intNonDispMembers = null;
        KnXDMSubscriberInfoDAO xdmSubscriberInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        intNonDispMembers = xdmSubscriberInfoDAO.getInternalNonDispatchMember(mdnList, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return intNonDispMembers;
    }


    @Override
    public ArrayList<String> getDispContacts(Collection<String> nonDispGrpMember, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getDispContacts(List<String> nonDispGrpMember, int corpId, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "nonDispGrpMember - ", nonDispGrpMember);
        ArrayList<String> intNonDispMembers = null;
        KnXDMCorpContactListDAO corpContactListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        intNonDispMembers = corpContactListDAO.getDispContacts(nonDispGrpMember, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return intNonDispMembers;
    }

    @Override
    public Map<String, Boolean> getLocationPubFeaturebit(ArrayList<String> pocHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getLocationPubFeaturebit(String pocHome, KnPersisterTxn persisterTxn)";
        Map<String, Boolean> isLocationPubEnabled;
        KnXDMLocationServiceConfigDAO corpContactListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMLocationServiceConfigDAO(pttServerId);
        isLocationPubEnabled = corpContactListDAO.getLocationPubFeaturebit(pocHome, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return isLocationPubEnabled;
    }

    /**
     * This method is to return the activation code generation and expiry time for a subscribers.
     *
     * @param mdn
     * @param clientType
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public KnCorpSubscriberDTO getActCodeExtTime(String mdn, int clientType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpActivationDAO activationDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpActivationDAO(pttServerId);
        return activationDao.getActCodeExtTime(mdn, clientType, readOnly, persisterTxn);
    }

    /**
     * @param corpId       corporate id
     * @param persisterTxn
     * @return count of subscriber in a corporate, return 0 if there is no subscriber in a corporate
     * @throws KnDAOException
     */
    @Override
    public int getInternalSubscriberCount(int corpId, boolean readOnly,
                                          KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.getInternalSubscriberCount(corpId, readOnly, persisterTxn);
    }

    /**
     * @param corpId       corporate id for which subscriber count need to fetch
     * @param contactType  1 if the subscriber is External Subscriber and 1 if the subscriber is NNI Subscriber
     * @param persisterTxn
     * @return count for External Subscriber OR NNI Subscriber depending on passed contactType
     * @throws KnDAOException
     */
    @Override
    public int getExternalSubscriberCount(int corpId, int contactType, boolean readOnly,
                                          KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMExtPoCSubscriberDAO extSubscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMExtPoCSubscriberDAO(pttServerId);
        return extSubscDAO.getExternalSubscriberCount(corpId, contactType, readOnly, persisterTxn);
    }


    /**
     * @param corpId       corporate id
     * @param filterType   possible value 0/1/2/3 0[All], other then 0 with pagination
     * @param fetchSize    number of Row to fetch
     * @param nextToken    the page number to fetch
     * @param sortType     By [1]MDN OR By [2]NAME, 0 means no sorting
     * @param persisterTxn
     * @return List of External Subscriber
     * @throws KnDAOException
     */
    @Override
    public List<KnExtSubsDetailsDTO> getExternalSubscriberList(int corpId, Integer filterType,
                                                               Integer fetchSize, Integer nextToken, Integer sortType, boolean readOnly,
                                                               KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMExtPoCSubscriberDAO extSubscDAO = KnCorpDBTablesRegistry
                .getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(
                        pttServerId);
        return extSubscDAO.getExternalSubscriberList(corpId, filterType, fetchSize, nextToken,
                sortType, readOnly, persisterTxn);
    }

    /**
     * @param corpId       corporate id
     * @param filterType   possible value 0/1/2/3 0[All], other then 0 with pagination
     * @param fetchSize    number of Row to fetch
     * @param nextToken    the page number to fetch
     * @param sortType     By [1]MDN OR By [2]NAME, 0 means no sorting
     * @param persisterTxn
     * @return List of NNI Subscriber
     * @throws KnDAOException
     */
    @Override
    public List<KnExtSubsDetailsDTO> getNniSubscriberList(int corpId, Integer filterType,
                                                          Integer fetchSize, Integer nextToken, Integer sortType, boolean readOnly,
                                                          KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMExtPoCSubscriberDAO extSubscDAO = KnCorpDBTablesRegistry
                .getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(
                        pttServerId);
        return extSubscDAO.getNniSubscriberList(corpId, filterType, fetchSize, nextToken,
                sortType, readOnly, persisterTxn);
    }

    /**
     * @param keySet       list of subscriber which contact count need to fetch
     * @param persisterTxn
     * @return map of subscriber and its contact count
     * @throws KnDAOException
     */
    @Override
    public Map<String, Integer> getSubscriberContactCount(Set<String> keySet, boolean readOnly,
                                                          KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry
                .getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.getSubscriberContactCount(keySet, readOnly, persisterTxn);
    }

    /**
     * This method query DG.ExtSubscrInfo table to return the profileId in KnCorpSubscriberDTO of each mdn list.
     *
     * @param extSubsList
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public Map<String, KnCorpSubscriberDTO> getExtSubsMap(List<String> extSubsList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnExtSubscrInfoDAO extSubscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createExtSubscrInfoDAO(pttServerId);
        return extSubscDAO.getExtSubsMap(extSubsList, readOnly, persisterTxn);
    }

    /**
     * This method returns the distinct profileId and profile details for MDN in extSubslist.
     *
     * @param extSubsList
     * @param persisterTxn
     * @return Map<Integer, KnExtProfileDetails>
     * @throws KnDAOException
     */
    public List<Integer> getExtSubsrProfilelist(List<String> extSubsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnExtSubscrInfoDAO extSubscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createExtSubscrInfoDAO(pttServerId);
        return extSubscDAO.getExtSubsrProfilelist(extSubsList, persisterTxn);
    }

    public List<KnCorpSubscriberDTO> getSubscPrivateMemberList(int privateContactListId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListMemberDAO corpListMemberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        return corpListMemberDAO.getSubscPrivateMemberList(privateContactListId, persisterTxn);
    }

    public List<KnCorpSubscriberDTO> getSublistDistinctMembers(Collection<Integer> addedSublistIds, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListMemberDAO corpListMemberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        return corpListMemberDAO.getSublistDistinctMembers(addedSublistIds, corpId, persisterTxn);
    }

    public Map<String, KnCorpSubscriberDTO> getSubsribersCorporateDetails
            (Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getSubsribersCorporateDetails(mdnList, persisterTxn);
    }

    public Collection<KnCorpGroupMemberDTO> getMemDetsils(int groupId, String mdn, KnPersisterTxn
            persisterTxn, String pttServerId) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getMemDetsils(groupId, mdn, persisterTxn);
    }

    public KnMdnDetailsPersistDTO getPoCSubscriberDetails
            (Collection<String> mdnList,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getPoCSubscriberDetails(mdnList,readOnly, persisterTxn);
    }

    /**
     * This method is used to get all the groups and the name and etag informatiom
     *
     * @param groupIds
     * @param persisterTxn
     * @throws KnDAOException
     */
    public ArrayList<KnCorpGroupDTO> getGrpsNameEtagInfo(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getGrpsNameEtagInfo(groupIds, persisterTxn);
    }

    /**
     * This method to the get the valid internal and external subscribers in the corporation from MDN list.
     *
     * @param mdnList
     * @param corpId
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    public List<KnCorpSubscriberDTO> getCorpSubscrDetails(List<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getCorpSubscrDetails(mdnList, corpId, persisterTxn);
    }

    public Map<String, Boolean> getSubscrBCGrpBit(List<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getSubscrBCGrpBit(mdnList, corpId, persisterTxn);
    }

    public void addToCorpGroupDistInfo(List<String> distMdn, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupDistInfoDAO corpGroupDistDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpGroupDistInfoDAO(pttServerId);
        corpGroupDistDAO.insertToCorpGroupDistInfo(distMdn, groupId, persisterTxn);
    }

    public List<String> selectGroupMemberList(int grpId,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        return groupMemberListDAO.selectGroupMemberList(grpId,readOnly, persisterTxn);
    }

    public List<String> deleteGroupDistInfo(List<String> mdnList, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupDistInfoDAO corpGroupDistDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpGroupDistInfoDAO(pttServerId);
        return corpGroupDistDAO.deleteGroupDistInfo(mdnList, groupId, persisterTxn);
    }

    public void deleteGroupDistInfo(Map<Integer, List<String>> groupMemMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupDistInfoDAO corpGroupDistDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpGroupDistInfoDAO(pttServerId);
        corpGroupDistDAO.deleteGroupDistInfo(groupMemMap, persisterTxn);
    }

    public List<Integer> getBroadcstGroupList(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        return groupMemberListDAO.getBroadcstGroupList(mdn, persisterTxn);
    }

    public void updateGrpBroadcasters(Map<Integer, List<String>> groupMemMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        groupMemberListDAO.updateGrpBroadcasters(groupMemMap, persisterTxn);
    }

    public Map<Integer, Integer> getGroupMemCount(Collection<Integer> groupIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberCountDAO groupMemberCountDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberCountDAO(pttServerId);
        return groupMemberCountDAO.getGroupMemCount(groupIds, readOnly, persisterTxn);
    }

    public List<Integer> getSupervisorGroups(String mdn, int supervisor, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        return groupMemberListDAO.getSupervisorGroups(mdn, supervisor, persisterTxn);
    }

    public List<Integer> getSupervisorGroups(List<String> mdnList, int supervisor, KnPersisterTxn persisterTxn) throws KnDAOException{
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        return groupMemberListDAO.getSupervisorGroups(mdnList, supervisor, persisterTxn);
    }

    public Set<Integer> getGrpDispMemList(List<Integer> dispatcherGrpList, String MDN, String pttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        return groupMemberListDAO.getGrpDispMemList(dispatcherGrpList, MDN, pttServerId, persisterTxn);
    }

    public Set<Integer> getGrpIdsHavingAtleastOneMember(List<Integer> dispatcherGrpList, String MDN, String pttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        return groupMemberListDAO.getGrpIdsHavingAtleastOneMember(dispatcherGrpList, MDN, pttServerId, persisterTxn);
    }

    public void updateGrpMemListProperties(Collection<KnCorpGroupMemberDTO> modifiedMembers, int grpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberListDAO(pttServerId);
        groupMemberListDAO.updateGrpMemListProperties(modifiedMembers, grpId, persisterTxn);
    }

    public void modifyGroupOverrdeDND(int overrideDnd, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.modifyGroupOverrdeDND(overrideDnd, groupId, persisterTxn);
    }

    public ArrayList<KnCorpGroupDTO> getGroupBasicInfoList(Collection<Integer> groupIds,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getGroupBasicInfoList(groupIds,readOnly, persisterTxn);
    }

    public List<KnLicensePackDTO> getLicenseProfileList(int corpId, String corpName, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMLicensePackListInfoDAO licensePackListInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMLicensePackListInfoDAO(pttServerId);
        return licensePackListInfoDAO.getBillingProfilesForCorpId(corpId, corpName, readOnly, persisterTxn);
    }

    public Set<Integer> groupsPushedToSublists(Collection<Integer> removeSublistIds, String pttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.groupsPushedToSublists(removeSublistIds, persisterTxn);
    }


    public List<KnLicenseSubDTO> getLicenseSubscriber(int pamAccId, int corpId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getLicenseSubscriber(int,  int,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "pamAcId - ", pamAccId, ", corpId - ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<KnLicenseSubDTO> mdnLists = new ArrayList<KnLicenseSubDTO>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            //SELECT MDN, SUBSCRNAME, SERVICEAUTHSTATUS FROM DG.POCSUBSCRINFO  WHERE PAMACCID = ? and CORPID = ?
            if (corpId > 0) {
                query = queryMapper.getQuery(GET_PSUEDO_MDNS);
            } else {
                query = queryMapper.getQuery(GET_PSUEDO_MDNS_WCSR);
            }
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            if (corpId > 0) {
                pstmt.setInt(2, corpId);
            }
            pstmt.setInt(1, pamAccId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                KnLicenseSubDTO subDTO = new KnLicenseSubDTO();
                subDTO.setMdn(rs.getString(1).trim());
                //multilingual revert change
                if (rs.getString(2) != null) {
                    try {
                        subDTO.setName(new String(rs.getString(2).getBytes("8859_1"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        knLogger.error("UnsupportedEncodingException while parsing subsc name ", e);
                    }
                }
                subDTO.setServiceAuthStatus(rs.getInt(3));
                subDTO.setSubsClientType(rs.getInt(4));
                subDTO.setPublicSubsType(rs.getInt(5));
                subDTO.setCorpSubsType(rs.getInt(6));
                mdnLists.add(subDTO);
            }
            if (mdnLists.isEmpty()) {
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Pam info not found for corp Profile .",
                        pttServerId, KnDAOSourceTypes.LICENSEINFO, query);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetching the License Subscriber from DB",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pstmt);
        }
        knLogger.debug(methodName, "License Subscriber from DB - ", mdnLists.size());
        return mdnLists;

    }

    public KnCorpSubscriberDTO getPamAccId(String billingNumber, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getPamAccId(int,boolean readOnly,  KnPersisterTxn)";
        knLogger.debug(methodName, billingNumber);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Integer pamAccId = null;
        KnCorpSubscriberDTO corpSubscriberDTO = new KnCorpSubscriberDTO();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_PAMACC_ID);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, billingNumber);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            if (rs.next()) {
                corpSubscriberDTO.setPamAccId(rs.getInt(1));
                corpSubscriberDTO.setEtag(rs.getLong(2));
            } else {
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Pam info not found.",
                        pttServerId, KnDAOSourceTypes.LICENSEINFO, query);
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetching PamAccId",
                    pttServerId, KnDAOSourceTypes.LICENSEINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        knLogger.exit(methodName, pamAccId);
        return corpSubscriberDTO;

    }

    @Override
    public List<String> getMarkForDeletionPseudoMdns(int pamAccId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMarkForDeletionPseudoMdns(pamAccid,boolean,  KnPersisterTxn)";
            knLogger.debug(methodName, pamAccId);
            Connection conn;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            String query = null;
            List<String> mdns = new ArrayList<String>();
            try {
                KnQueryMapper queryMapper = KnQueryMapper.getInstance();
                query = queryMapper.getQuery(GET_KUIDS_BY_STATUS);
                conn = persisterTxn.getDBConnection(pttServerId, readOnly);
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, MARK_FOR_DELETION);
                pstmt.setInt(2, pamAccId);
                knLogger.debug(methodName, "Executing query - ", query);
                rs = pstmt.executeQuery();
                knLogger.debug(methodName, "Query executed successfully.");
                while (rs.next()) {
                    mdns.add(rs.getString(1).trim());
                }
            } catch (SQLException e) {
                throw KnDbUtil.processException(e, "Failed while fetching the marked pseudo mdns from DB",
                        pttServerId, KnDAOSourceTypes.KUIDPOOLUSAGE, query);
            } finally {
                KnDbUtil.closeResultSet(rs);
                KnDbUtil.closePreparedStatement(pstmt);
            }

            return mdns;
    }


    public void updateMarkList(Collection<String> marklist, int pamAccId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateMarkList(pamAccid, status, KnPersisterTxn)";
        knLogger.debug(methodName, marklist, xdmsHomePttId, persisterTxn);
        Connection conn;
        PreparedStatement pStmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(INSERT_KUID);
            knLogger.debug(methodName, "Executing query - ", query);
            conn = persisterTxn.getDBConnection(xdmsHomePttId, false);
            pStmt = conn.prepareStatement(query);
            for (String mdn : marklist) {
                pStmt.setString(1,mdn);
                pStmt.setInt(2,pamAccId );
                pStmt.setInt(3, MARK_FOR_DELETION);
                pStmt.addBatch();
            }
            int[] count = pStmt.executeBatch();
            knLogger.debug(methodName, "Query: Executed count:", count);
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while inserting  marked mdns ",
                    pttServerId, KnDAOSourceTypes.KUIDPOOLUSAGE, query);
        } finally {
            KnDbUtil.closeStatement(pStmt);
        }
    }

    @Override
    public void updateBillingName(String billingMDN, String billingName, String xdmsHomePttId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "updateBillingName(billingMDN, billingName, xdmsHomePttId, KnPersisterTxn)";
        knLogger.debug(methodName, KnGDPRTemplate.mdn(billingMDN), KnGDPRTemplate.name(billingName), xdmsHomePttId, persisterTxn);
        String query = null;
        Connection conn;
        PreparedStatement pstmt = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_BILLING_NAME);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            //multilingual revert changes
            if (billingName != null) {
                try {
                    billingName = new String(billingName.getBytes("UTF-8"), "8859_1");
                } catch (UnsupportedEncodingException e) {
                    knLogger.error("UnsupportedEncodingException while parsing billing name ", e);
                }
            }
            pstmt.setString(1, billingName);
            pstmt.setString(2, billingMDN);
            knLogger.debug(methodName, "Executing query - ", query);
            int status = pstmt.executeUpdate();
            knLogger.debug(methodName, "Query executed successfully. - " + status);
            if (status == 0) {
                throw new KnDBPersistenceException(KnErrorCodes.DAO.ROW_NOT_FOUND, "Billing account not found.",
                        pttServerId, KnDAOSourceTypes.LICENSEINFO, query);
            }

        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed to update Billing Name", pttServerId, KnDAOSourceTypes.LICENSEINFO, query);
        } finally {
            KnDbUtil.closePreparedStatement(pstmt);
        }
    }


    public void updateUnMarkList(Collection<String> unMarklist, int pamAccId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateUnMarkList(unMarklist, pamAccId,xdmsHomePttId, KnPersisterTxn)";
        knLogger.debug(methodName, unMarklist,pamAccId, xdmsHomePttId, persisterTxn);
        Connection conn;
        Statement stmt = null;
        String query = null;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(DELETE_KUID);
            query = replaceContactWithValue(query, MDNLIST, formCommaSeperatedIdList(unMarklist));
            query = KnDbUtil.replaceValInQry(query, pamAccId);
            knLogger.debug(methodName, "Executing query - ", query);
            conn = persisterTxn.getDBConnection(xdmsHomePttId, false);
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
            knLogger.debug(methodName, "Executed query successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetching the distinct members from DB",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(stmt);
        }
    }

    public List<Integer> getAllPamAccountIdforCorp(int corpId, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAllPamAccountIdforCorp(corpId, status,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, corpId, xdmsHomePttId, persisterTxn);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        List<Integer> pamAccIds = new ArrayList<Integer>();
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_All_PAMACC_ID_FOR_CORP);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, corpId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Query executed successfully.");
            while (rs.next()) {
                pamAccIds.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Failed while fetching the distinct members from DB",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePreparedStatement(pstmt);
        }
        return pamAccIds;
    }

    public long updatePamAccountEtag(int pamAccId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updatePamAccountEtag(pamAccId, status, KnPersisterTxn)";
        knLogger.debug(methodName, pamAccId, xdmsHomePttId, persisterTxn);
        Connection conn;
        PreparedStatement pstmt = null;
        String query = null;
        long currentTime = System.currentTimeMillis();
        try {

            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(UPDATE_PAMACCOUNT_ETAG);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pstmt = conn.prepareStatement(query);
            pstmt.setLong(1, currentTime);
            pstmt.setInt(2, pamAccId);
            knLogger.debug(methodName, "Executing query - ", "'", query, "'");
            pstmt.executeUpdate();
            knLogger.debug(methodName, "Query executed successfully");
        } catch (SQLException e) {
            throw KnDbUtil.processException(e, "Error occurred while updating pamaccountId etag",
                    pttServerId, KnDAOSourceTypes.XDM_CORP_SUBSCRIBER_INFO, query);
        } finally {
            KnDbUtil.closeStatement(pstmt);
        }
        return currentTime;
    }

    /**
     * This method return true is the request mdn exist as external contact in the corpid.
     *
     * @param mdn
     * @param corpid
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    @Override
    public boolean isExternalContExist(String mdn, int corpid, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMExtPoCSubscriberDAO extSubscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMExtPoCSubscriberDAO(pttServerId);
        return extSubscDAO.isExternalContExist(mdn, corpid, readOnly, persisterTxn);
    }

    /**
     * This method is the retrieve the list of sublist where MDN exist a member.
     *
     * @param mdn
     * @param persisterTxn
     * @return
     * @throws KnDAOException
     */
    @Override
    public List<Integer> getSubscrAllSublists(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListMemberDAO corpListMemberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        return corpListMemberDAO.getSubscrAllSublists(mdn, readOnly, persisterTxn);
    }

    @Override
    public Map<Integer, KnCorpSublistDTO> filterSublists(List<Integer> sublistIds, int corpId, int
            sublistType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListInfoDAO corpListInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListInfoDAO(pttServerId);
        return corpListInfoDAO.filterSublists(sublistIds, corpId, sublistType, readOnly, persisterTxn);
    }

    @Override
    public List<KnMDNInfoDto> getSubscrReverseContacts(ArrayList<Integer> sublistIds, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberInfoDAO.getSubscrReverseContacts(sublistIds, readOnly, persisterTxn);
    }

    @Override
    public Map<Integer, List<String>> getSubscriberGroupIds(List<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupDistInfoDAO corpGroupDistInfo = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupDistInfoDAO(pttServerId);
        return corpGroupDistInfo.getSubscriberGroupIds(mdnList, readOnly, persisterTxn);
    }

    @Override
    public Map<Integer, List<String>> getCorpGroupIds(Collection<Integer> corpGroupIdList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupDistInfoDAO corpGroupDistInfo = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupDistInfoDAO(pttServerId);
        return corpGroupDistInfo.getCorpGroupIds(corpGroupIdList, readOnly, persisterTxn);
    }

    @Override
    public KnCorpGWLinkedAccountInfoDTO getCorporateLinkedAccountInfo(String refId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGWLinkedAccountInfoDAO corpGWLinkedAccountInfo = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorGWLinkedAccountInfoDAO(pttServerId);
        return corpGWLinkedAccountInfo.getCorporateLinkedAccountInfo(refId, readOnly, persisterTxn);
    }


    @Override
    public List<String> getGroupMdnInList(Collection<String> mdnList, int corpId,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscInfoDO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscInfoDO.getGroupMdnInList(mdnList, corpId,readOnly, persisterTxn);
    }

    @Override
    public List<KnCorpSubscriberDTO> getGroupMdnInListDTO(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscInfoDO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscInfoDO.getGroupMdnInListDTO(mdnList, corpId, persisterTxn);
    }

    @Override
    public Map<String, Integer> selectSubscrGrpCounts(List<String> finalGroupMembersinDB, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO subscInfoDO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return subscInfoDO.selectSubscrGrpCounts(finalGroupMembersinDB, corpId, persisterTxn);
    }

    @Override
    public Collection<Integer> selectGroupIdList(int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getGroupIdList(corpId, persisterTxn);
    }

    @Override
    public List<String> getSublistMembers(List<Integer> sublistIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListMemberDAO listMemDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        return listMemDAO.getSublistMembers(sublistIds, persisterTxn);
    }


    @Override
    public Map<String, Boolean> getTgscFeatureBit(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscInfoDO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscInfoDO.getTgscFeatureBit(mdnList, persisterTxn);
    }


    public Map<String, Integer> getCampedGroupCount(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpCampGrpDOA campInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCampedGrpDAO(pttServerId);
        return campInfoDAO.getCampedGrpCount(mdnList, persisterTxn);
    }


    public Map<String, Integer> getSubscTgscDocEtag(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpCampGrpDOA campInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCampedGrpDAO(pttServerId);
        return campInfoDAO.getTGSCDocumentEtag(mdnList, persisterTxn);
    }

    @Override
    public Collection<String> getMdnSpecificToClient(int corpId, int client_type, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscInfoDO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscInfoDO.getMdnSpecificToClient(corpId, client_type, persisterTxn);
    }

    @Override
    public Collection<String> getMdnsSpecificToClients(int corpId, Collection<Integer> client_types, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscInfoDO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscInfoDO.getMdnsSpecificToClients(corpId, client_types, readOnly, persisterTxn);
    }

    @Override
    public boolean getIsGWEnabledForCorp(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscInfoDO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscInfoDO.getIsGWEnabledForCorp(corpId, persisterTxn);
    }


    public void deleteSublistMembersForRequestMDN(Collection<Integer> sublistIds, String MDN,
                                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteSublistMembersForRequestMDN(Collection<Integer>,String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListMemberDAO corpListMemberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListMemberDAO(pttServerId);
        corpListMemberDAO.deleteSublistMembersForRequestMDN(sublistIds, MDN, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    /**
     * This method is used to fetch the supervisor of the group with is_supervisory as 1.
     *
     * @param groupIds
     * @param persisterTxn
     * @return
     */
    public Set<Integer> getGroupSupervisorList(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO goupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return goupMemberListDAO.getGroupSupervisorList(groupIds, persisterTxn);
    }

    @Override
    public KnCorpSublistDTO getSublistDetailsByName(int corpId, String subPrefix, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListInfoDAO sublistInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListInfoDAO(pttServerId);
        return sublistInfoDAO.getSublistDetailsByName(corpId, subPrefix, persisterTxn);
    }

    @Override
    public KnCorpGroupDTO getGroupDetailsByName(String grpPrefix, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getGroupDetailsByName(corpId, grpPrefix, persisterTxn);
    }

    @Override
    public Map<Integer, Boolean> getClientTypeConfigDetails(KnPersisterTxn persisterTxn) throws KnDAOException {
        KnClientTypeConfigurationInfoDAO clientTypeConfiguration = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createClientTypeConfigurationDAO(pttServerId);
        return clientTypeConfiguration.getClientTypeConfigDetails(persisterTxn);
    }

    @Override
    public Set<Integer> getCorporatePamClientTypes(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnPAMSubscriberProfileInfoDAO pamSubscriberProfileInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createPAMSubscriberProfileInfoDAO(pttServerId);
        return pamSubscriberProfileInfoDAO.getCorporatePamClientTypes(corpId, persisterTxn);
    }

    public List<KnCorpTGSPersistDTO> getSubsChannelGrp(KnIPTalkGroupDTO ipTalkGroupDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpChannelGrpDAO xdmChannelGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMChannelGrpDAO(pttServerId);
        return xdmChannelGrpDAO.getSubsChannelGrp(ipTalkGroupDTO, persisterTxn);
    }

    /**
     * This method get the mdn info.
     *
     * @param mdnList
     * @param persisterTxn
     */
    public Map<String, Integer> getPoCSubsDetails(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpContactListDAO contactListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpContactListDAO(pttServerId);
        return contactListDAO.getPoCSubsDetails(mdnList, persisterTxn);
    }

    @Override
    public Map<String, Integer> updateAndGetDirecEtag(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMDirectoryDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMDirectoryDAO(pttServerId);
        return subscriberTable.getAndUpdateDirectoryEtag(mdnList, persisterTxn);
    }

    @Override
    public KnSubscrFeatureSetRespDTO getSubscriberFeatureSets(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.getSubscriberFeatureSets(corpId, readOnly, persisterTxn);
    }

    @Override
    public Map<String, KnCorpSubscriberDTO> getSubscrFeatureBitDetails(Set<String> reqMdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.getSubscrFeatureBitDetails(reqMdnList, corpId, persisterTxn);
    }

    @Override
    public void updateSubscrProfiles(Map<String, KnCorpSubscriberDTO> updateSubscrMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        subscriberTable.updateSubscrProfiles(updateSubscrMap, persisterTxn);
    }

    @Override
    public List<KnCorpSubscriberDTO> getCorpExtContact(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMExtPoCSubscriberDAO extSubscriberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMExtPoCSubscriberDAO(pttServerId);
        return extSubscriberDAO.getCorpExtContact(corpId, persisterTxn);
    }

    public Collection<String> activationCodeExistForMDNs(Collection<String> mdnList, String serviceName, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpActivationDAO activationDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpActivationDAO(pttServerId);
        return activationDao.activationCodeExistForMDNs(mdnList, serviceName, persisterTxn);
    }


    public void deleteActivationCode(Collection<String> mdns, String serviceName, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpActivationDAO activationDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpActivationDAO(pttServerId);
        activationDao.deleteActivationCode(mdns, serviceName, persisterTxn);
    }


    public String getSubscribersOTP(String mdn, String serviceName, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpActivationDAO activationDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpActivationDAO(pttServerId);
        return activationDao.getSubscribersOTP(mdn, serviceName, persisterTxn);
    }

    @Override
    public void updateCorpSubscriber(KnIPSubscriberInfoDTO corpSubscriberDTO,List<String> profileMdns,KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        subscriberTable.updateCorpSubscriber(corpSubscriberDTO,profileMdns, persisterTxn);
    }

    @Override
    public void updateSubscriberServiceAuthStatus(KnIPSubscriberInfoDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        subscriberTable.updateSubscriberServiceAuthStatus(corpSubscriberDTO, persisterTxn);
    }

    @Override
    public String getUserIdProfile(KnIPSubscriberInfoDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.getUserIdProfile(corpSubscriberDTO, persisterTxn);
    }

    @Override
    public Collection<KnCorpLITargetProfile> getLITargetInfo(KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMLISubscriberInfoDAO liSubscriberProfileInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMLISubscriberInfoDAO(pttServerId);
        return liSubscriberProfileInfoDAO.getLITargetInfo(persisterTxn);
    }

    @Override
    public void deleteActivationCodeForMDN(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpActivationDAO activationDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpActivationDAO(pttServerId);
        activationDao.deleteActivationCodeForMDN(mdn, persisterTxn);
    }

    @Override
    public void deleteActivationCodeForMDN(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpActivationDAO activationDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpActivationDAO(pttServerId);
        activationDao.deleteActivationCodeForMDN(mdnList, persisterTxn);
    }

    @Override
    public KnCorpSubscriberDTO getUserProfile(KnIPSubscriberInfoDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.getUserProfile(corpSubscriberDTO, persisterTxn);
    }

    @Override
    public void modifyGroupLmrInteropCapable(int lmrInteropCapable, Collection<Integer> groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyGroupLmrInteropCapable(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.modifyGroupLmrInteropCapable(lmrInteropCapable, groupId, persisterTxn);
        knLogger.debug(methodName, "lmrInteropCapable modified successfully");
    }

    @Override
    public void modifyGroupUGWParameter(int ugwInterop, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyGroupUGWParameter(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.modifyGroupUGWParameter(ugwInterop, groupId, persisterTxn);
        knLogger.debug(methodName, "ugwInterop modified successfully");
    }

    @Override
    public void modifyGroupRecordingFsParameter(int recordingFs, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyGroupRecordingFsParameter(int, int, KnPersisterTxn)";
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.modifyGroupRecordingFsParameter(recordingFs, groupId, persisterTxn);
        knLogger.debug(methodName, "recordingFs modified successfully");
    }
    @Override
    public void modifyAuthorizedLargeTGParameter(int authorizedLargeTG, int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyAuthorizedLargeTGParameter(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.modifyAuthorizedLargeTGParameter(authorizedLargeTG, groupId, persisterTxn);
        knLogger.debug(methodName, "authorizedLargeTG modified successfully");
    }
    @Override
    public Map<Integer, Integer> getSGCorpGroupMembersCount(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO grpMemListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return grpMemListDAO.getSGCorpGroupMembersCount(groupIds, persisterTxn);
    }

    @Override
    public Map<String, Long> getTargetUserPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getTargetUserPermissions(ipAuthUserPermissionInfoDTO, readOnly, persisterTxn);
    }

    @Override
    public Map<String, KnMcpttPermissionDTO> getAuthUserPermissions(String authMdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getAuthUserPermissions(authMdn, readOnly, persisterTxn);
    }

    @Override
    public Map<String, KnMcpttPermissionDTO> getTargUserPermissions(String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getTargUserPermissions(targetMdn, persisterTxn);
    }

    @Override
    public void insertIntoMcpttPermInfo(Collection<KnMcpttPermissionDTO> mcpttMappingDto, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.insertIntoMcpttPermInfo(mcpttMappingDto, persisterTxn);
    }

    @Override
    public void deleteFromMcpttPermInfoAuthMdn(String authMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.deleteFromMcpttPermInfoAuthMdn(authMdn, persisterTxn);
    }

    @Override
    public void deleteFromMcpttPermInfoAuthMdn(List<String> authMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.deleteFromMcpttPermInfoAuthMdn(authMdn, persisterTxn);
    }

    @Override
    public void deleteFromMcpttPermInfoTargetMdn(String targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.deleteFromMcpttPermInfoTargetMdn(targetMdn, persisterTxn);
    }

    @Override
    public void deleteFromMcpttPermInfoTargetMdn(List<String> targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.deleteFromMcpttPermInfoTargetMdn(targetMdn, persisterTxn);
    }

    @Override
    public void updateToMcpttPermInfo(Collection<KnMcpttPermissionDTO> mcpttMappingDto, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.updateToMcpttPermInfo(mcpttMappingDto, persisterTxn);
    }

    public void updateToMcpttPermInfoForPrivacyStatus(Map<String,Integer> mapListForPrivacy, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.updateToMcpttPermInfoForPrivacyStatus(mapListForPrivacy,persisterTxn);
    }

    @Override
    public void deleteFromMcpttPermInfo(String authMdn, Collection<String> targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.deleteFromMcpttPermInfo(authMdn, targetMdn, persisterTxn);
    }

    @Override
    public void deleteFromMcpttPermInfo(Map<String, String> authTargetMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.deleteFromMcpttPermInfo(authTargetMap, persisterTxn);
    }

    @Override
    public void deleteFromMcpttPerm(Map<String, Collection<String>> authTargetMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.deleteFromMcpttPerm(authTargetMap, persisterTxn);
    }

    public Map<String, Integer> getAuthMdnList(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getAuthMdnList(ipAuthUserPermissionInfoDTO, readOnly, persisterTxn);
    }

    public Map<String, Integer> getAuthUserMappingCount(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getAuthUserMappingCount(mdnList, corpId, persisterTxn);
    }

    public Collection<String> getAuthMdnListFromTarget(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getAuthMdnListFromTarget(mdnList, corpId, persisterTxn);
    }

    public Map<String, KnOPDocChgDTO> deleteFromAuthorizationInfo(Collection<String> authMdnList, Map<String, KnOPDocChgDTO> authorizationMap,
                                                                   KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.deleteFromAuthorizationInfo(authMdnList, authorizationMap, persisterTxn);
    }

    public Map<String, Long> seleteFromAuthDoc(Collection<String> authMdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.seleteFromAuthDoc(authMdnList, persisterTxn);
    }

    public void insertIntoAuthDoc(Collection<String> authMdnList, Map<String, Long> authEtagMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.insertIntoAuthDoc(authMdnList, authEtagMap, persisterTxn);
    }

    public Map<String, KnOPDocChgDTO> insertOrUpdateAuthorizationInfo(Collection<String> authMdnList, Map<String, KnOPDocChgDTO> authorizationMap,
                                                                       KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.insertOrUpdateAuthorizationInfo(authMdnList, authorizationMap, persisterTxn);
    }

    public Map<String, Integer> getValidGrpTypeInCorp(Collection<String> grpIdList, int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        return corpGroupInfoDAO.getValidGrpTypeInCorp(grpIdList, corpId, persisterTxn);
    }

    @Override
    public Map<String, String> selectGroupMemberForGroupIds(Collection<Integer> groupIds, String memberMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO grpMemListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return grpMemListDAO.selectGroupMemberForGroupIds(groupIds, memberMdn, persisterTxn);
    }

    @Override
    public Collection<KnSubsDestEmergencyAttributes> getEmergDestAttributes(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getEmergDestAttributes(mdn, readOnly, persisterTxn);
    }

    @Override
    public Map<String, Collection<String>> getEmergUserDestMap(Collection<String> emergUserList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getEmergUserDestMap(emergUserList, persisterTxn);
    }

    @Override
    public Map<String, Collection<String>> getEmergDestUserMap(Collection<String> emergDestList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getEmergDestUserMap(emergDestList, persisterTxn);
    }

    @Override
    public Collection<KnSubsDestEmergencyAttributes> getEmergDestAttributesForDestination(String destination, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getEmergDestAttributesForDestination(destination, readOnly, persisterTxn);
    }

    @Override
    public KnSubsEmergencyAttributes getEmergSubsAttributes(String mdn, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getEmergSubsAttributes(mdn, corpId, readOnly, persisterTxn);
    }

    @Override
    public void deleteFromEmergSubsDestInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.deleteFromEmergSubsDestInfo(mdn, persisterTxn);
    }

    @Override
    public void deleteFromEmergSubsDestInfo(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.deleteFromEmergSubsDestInfo(mdnList, persisterTxn);
    }

    @Override
    public void deleteFromEmergInfoForDest(String emergDest, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.deleteFromEmergInfoForDest(emergDest, persisterTxn);
    }

    @Override
    public void deleteFromEmergInfoForDest(List<String> emergDest, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.deleteFromEmergInfoForDest(emergDest, persisterTxn);
    }

    @Override
    public void updateToEmergSubsDestInfo(KnSubsEmergencyAttributes subsEmergencyAttributes, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.updateToEmergSubsDestInfo(subsEmergencyAttributes, persisterTxn);
    }

    @Override
    public void updateToEmergSubsDestAddInfo(KnSubsEmergencyAttributes subsEmergencyAttributes, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.updateToEmergSubsDestAddInfo(subsEmergencyAttributes, persisterTxn);
    }

    @Override
    public void insertIntoEmergSubsAttributes(Collection<KnSubsDestEmergencyAttributes> destEmergencyAttributes, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.insertIntoEmergSubsAttributes(destEmergencyAttributes, persisterTxn);
    }

    @Override
    public void updateEmergSubsAttributes(Collection<KnSubsDestEmergencyAttributes> destEmergencyAttributes, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.updateEmergSubsAttributes(destEmergencyAttributes, persisterTxn);
    }

    @Override
    public void deleteFromEmergDoc(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.deleteFromEmergDoc(mdnList, persisterTxn);
    }

    public Map<String, Long> seleteFromEmergDoc(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.seleteFromEmergDoc(mdnList, persisterTxn);
    }

    public void insertIntoEmergDoc(Collection<String> mdnList, Map<String, Long> emergEtagMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.insertIntoEmergDoc(mdnList, emergEtagMap, persisterTxn);
    }

    @Override
    public Map<String, KnOPDirChgDTO> updateEtag(Collection<String> mdnList, KnPersisterTxn persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException {
        KnXDMDirectoryDAO directoryDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMDirectoryDAO(pttServerId);
        return directoryDAO.updateEtag(mdnList, persisterTxn, etagMap);
    }

    @Override

    public Map<String, KnOPDirChgDTO> updateEtagForUpm(Collection<String> mdnList, KnPersisterTxn persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException {
        KnXDMDirectoryDAO directoryDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMDirectoryDAO(pttServerId);
        return directoryDAO.updateEtagForUpm(mdnList, persisterTxn, etagMap);
    }

    @Override

    public Map<String, KnOPDirChgDTO> selectEtag(Collection<String> mdnList, KnPersisterTxn persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException {
        KnXDMDirectoryDAO directoryDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMDirectoryDAO(pttServerId);
        return directoryDAO.selectEtag(mdnList, persisterTxn, etagMap);
    }

    @Override

    public Map<String, KnOPDocChgDTO> insertOrUpdateEmergencyInfo(Collection<String> authMdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.insertOrUpdateEmergencyInfo(authMdnList, persisterTxn);
    }

    @Override
    public void modifyGroupEmergAttributes(KnIPCorpGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyGroupEmergAttributes(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.modifyGroupEmergAttributes(groupInfoDTO, persisterTxn);
        knLogger.debug(methodName, "modifyGroupEmergAttributes modified successfully");
    }

    @Override
    public KnCorpGroupDTO getGroupIdByName(String grpPrefix, int clientIntf, String ownerMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getGroupIdByName(grpPrefix, clientIntf, ownerMdn, persisterTxn);
    }

    public Map<String, Integer> getGroupMembersClientTypeMap(Collection<String> mdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberInfoDAO.getGroupMembersClientTypeMap(mdnList, corpId, persisterTxn);
    }

    public Collection<KnCorpGroupInfoPersistDTO> selectTpGroupList(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.selectTpGroupList(corpId, readOnly, persisterTxn);
    }

    public boolean updateGroupOwner(String oldGroupOwner, String newGroupOwner, int corpID, KnPersisterTxn persisterTxn) throws KnDAOException{
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.updateGroupOwner(oldGroupOwner, newGroupOwner, corpID, persisterTxn);
    }

    public boolean updateGroupOwner(List<String> oldGroupOwner, String newGroupOwner, int corpID, KnPersisterTxn persisterTxn) throws KnDAOException{
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.updateGroupOwner(oldGroupOwner, newGroupOwner, corpID, persisterTxn);
    }

    public Collection<String> getSubscribersContactList(List<String> completeMdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscribersContactList(Collection<String>, int KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpContactListDAO corpListMemDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpContactListDAO(pttServerId);
        Collection<String> subsList = corpListMemDAO.getSubscribersContactList(completeMdnList, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return subsList;
    }

    public Collection<String> getSubscribersGroupList(Collection<Integer> groupIds, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getSubscribersGroupList(Collection<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupDistInfoDAO corpMemberListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupDistInfoDAO(pttServerId);
        return corpMemberListDAO.getSubscribersGroupList(groupIds, persisterTxn);
    }

    @Override
    public boolean isEmergencyDestExists(String emergDest, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.isEmergencyDestExists(emergDest, readOnly, persisterTxn);
    }

    public Map<String, Integer> getGroupMembersClientType(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberInfoDAO.getGroupMembersClientType(mdnList, persisterTxn);
    }

    public Collection<Integer> getCorpGroupCountIntf(int corpId, int intf, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpGroupCount(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getCorpGroupCountIntf(corpId, intf, persisterTxn);
    }

    public int getCorpGroupCountIntfPerOwner(int corpId, int intf, String grpOwner, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpGroupCount(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getCorpGroupCountIntfPerOwner(corpId, intf, grpOwner, persisterTxn);
    }

    public Map<String, Integer> selectSubscriberAbdgGroupCounts(Collection<Integer> groupIds, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "selectSubscriberAbdgGroupCounts(Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnCorpGroupDistInfoDAO groupDistInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupDistInfoDAO(pttServerId);
        return groupDistInfoDAO.selectSubscriberAbdgGroupCounts(groupIds, persisterTxn);
    }

    public Map<String, Integer> getExternalSubscriberAbdgGroupCount(Collection<String> externalMdnList,
                                                                int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getExternalSubscriberAbdgGroupCount(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMCorpGroupMemberListDAO goupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return goupMemberListDAO.getExternalSubscriberAbdgGroupCount(externalMdnList, corpId, persisterTxn);
    }

    public Collection<Integer> getOwnerGroupIds(String mdn, int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getOwnerGroupIds(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getOwnerGroupIds(mdn, corpId, persisterTxn);
    }

    public Collection<Integer> getOwnerGroupIds(List<String> mdnList, int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        String methodName = "getOwnerGroupIds(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getOwnerGroupIds(mdnList, corpId, persisterTxn);
    }

    @Override
    public String getAliasMdnProfile(KnIPSubscriberInfoDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.getAliasMdnProfile(corpSubscriberDTO, persisterTxn);
    }

    public KnCorpSubscriberDTO selectSubsInfo(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.selectSubsInfo(mdn, persisterTxn);
    }

    public KnCorpSubscriberDTO getPamAccountId(String billingNumber, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMLicensePackListInfoDAO licensePackListInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMLicensePackListInfoDAO(pttServerId);
        return licensePackListInfoDAO.getPamAccountId(billingNumber, persisterTxn);
    }

    public Map<Integer, String> getBillingNumber(Set<Integer>pamAccId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMLicensePackListInfoDAO licensePackListInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMLicensePackListInfoDAO(pttServerId);
        return licensePackListInfoDAO.getBillingNumber(pamAccId, persisterTxn);
    }

    public String getExtSubsrProfile(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnExtSubscrInfoDAO extSubscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createExtSubscrInfoDAO(pttServerId);
        return extSubscDAO.getExtSubsrProfile(mdn, persisterTxn);
    }

    @Override
    public void updateSubsEntities(KnIPSubscriberInfoDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        subscriberTable.updateSubsEntities(corpSubscriberDTO, persisterTxn);
    }

    @Override
    public Map<String, KnCorpSubsEntitiesDTO> getSubsEntitiesDetails(List<String> mdnList,boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.getSubsEntitiesDetails(mdnList,readOnly, persisterTxn);
    }

    @Override
    public Map<String, String> getMdnByUsingAliasMdn(List<String> aliasMdnList, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.getMdnByUsingAliasMdn(aliasMdnList, readOnly, persisterTxn);
    }

    @Override
    public Map<String, String> getMdnByUsingUserId(List<String> userIdList, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.getMdnByUsingUserId(userIdList, readOnly, persisterTxn);
    }

    @Override
    public Collection<KnCorpAddlTGInfoDTO> getSubsAddlTGList(String mdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpAddlTalkGrpDAO xdmCorpAddlTalkGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMAddlTGInfoDAO(pttServerId);
        return xdmCorpAddlTalkGrpDAO.getSubsAddlTGList(mdn, readOnly, persisterTxn);
    }

    @Override
    public Collection<KnCorpAddlTGInfoDTO> getSubsAddlTGList(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpAddlTalkGrpDAO xdmCorpAddlTalkGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMAddlTGInfoDAO(pttServerId);
        return xdmCorpAddlTalkGrpDAO.getSubsAddlTGList(mdnList, persisterTxn);
    }

    @Override
    public void deleteSubsAddlTGList(Collection<KnCorpAddlTGInfoDTO> corpAddlTGInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpAddlTalkGrpDAO xdmCorpAddlTalkGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMAddlTGInfoDAO(pttServerId);
        xdmCorpAddlTalkGrpDAO.deleteSubsAddlTGList(corpAddlTGInfoDTOS, persisterTxn);
    }

    @Override
    public void insertSubsAddlTGList(Collection<KnCorpAddlTGInfoDTO> corpAddlTGInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpAddlTalkGrpDAO xdmCorpAddlTalkGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMAddlTGInfoDAO(pttServerId);
        xdmCorpAddlTalkGrpDAO.insertSubsAddlTGList(corpAddlTGInfoDTOS, persisterTxn);
    }

    @Override
    public void deleteSubsAddlTalkGroup(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpAddlTalkGrpDAO xdmCorpAddlTalkGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMAddlTGInfoDAO(pttServerId);
        xdmCorpAddlTalkGrpDAO.deleteSubsAddlTalkGroup(mdnList, persisterTxn);
    }

    @Override
    public Map<String, KnOPDocChgDTO> insertOrUpdateAddlTGInfo(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpAddlTalkGrpDAO xdmCorpAddlTalkGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMAddlTGInfoDAO(pttServerId);
        return xdmCorpAddlTalkGrpDAO.insertOrUpdateAddlTGInfo(mdnList, persisterTxn);
    }

    @Override
    public Map<Integer, Collection<Integer>> getZoneChannelMap(KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpAddlTalkGrpDAO xdmCorpAddlTalkGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMAddlTGInfoDAO(pttServerId);
        return xdmCorpAddlTalkGrpDAO.getZoneChannelMap(persisterTxn);
    }

    @Override
    public void deleteSubsAddlTalkGroupDoc(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpAddlTalkGrpDAO xdmCorpAddlTalkGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMAddlTGInfoDAO(pttServerId);
        xdmCorpAddlTalkGrpDAO.deleteSubsAddlTalkGroupDoc(mdnList, persisterTxn);
    }

    @Override
    public Collection<KnCorpAddlTGInfoDTO> getSubsAddlDetails(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpAddlTalkGrpDAO xdmCorpAddlTalkGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMAddlTGInfoDAO(pttServerId);
        return xdmCorpAddlTalkGrpDAO.getSubsAddlDetails(groupIds, persisterTxn);
    }

    public Map<String, KnOPDocChgDTO> deleteFromSubsAddInfoInfo(Collection<String> mdnList, Map<String, KnOPDocChgDTO> addlTGMap,
                                                                KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpAddlTalkGrpDAO xdmCorpAddlTalkGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMAddlTGInfoDAO(pttServerId);
        return xdmCorpAddlTalkGrpDAO.deleteFromSubsAddInfoInfo(mdnList, addlTGMap, persisterTxn);
    }

    public Map<String, Long> getSubsAddlEtagMap(Collection<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpAddlTalkGrpDAO xdmCorpAddlTalkGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMAddlTGInfoDAO(pttServerId);
        return xdmCorpAddlTalkGrpDAO.getSubsAddlEtagMap(mdnList, readOnly, persisterTxn);
    }

    public void insertSubsAddlTGDoc(Map<String, Long> subsEtagMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpAddlTalkGrpDAO xdmCorpAddlTalkGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMAddlTGInfoDAO(pttServerId);
        xdmCorpAddlTalkGrpDAO.insertSubsAddlTGDoc(subsEtagMap, persisterTxn);
    }

    public List<KnCorpGroupDTO> getAllLargeGroups(KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getAllLargeGroups(persisterTxn);
    }

    @Override
    public void updateGrpMemListLocWatchers(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        groupMemberListDAO.updateGrpMemListLocWatchers(groupIds, persisterTxn);
    }

    @Override
    public void updateIsLargeGrpFlag(Map<Integer, Integer> groupLrgGrpFlagMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        groupInfoDAO.updateIsLargeGrpFlag(groupLrgGrpFlagMap, persisterTxn);
    }

    @Override
    public int getLrgAbdgGroupCount(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getLrgAbdgGroupCount(corpId, persisterTxn);
    }

    @Override
    public void deleteAllTGListGrpIds(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpAddlTalkGrpDAO xdmCorpAddlTalkGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMAddlTGInfoDAO(pttServerId);
        xdmCorpAddlTalkGrpDAO.deleteAllTGListGrpIds(groupIds, persisterTxn);
    }

    @Override
    public Map<String, KnCorpSubscriberDTO> getSubscriberAdditionalDetails(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getSubscriberAdditionalDetails(mdnList, persisterTxn);
    }

    @Override
    public Collection<String> getLocWatcherAndDispatcher(Collection<Integer> groupIdLst, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        return corpGroupInfoDAO.getLocWatcherAndDispatcher(groupIdLst, persisterTxn);
    }

    @Override
    public Map<String, Boolean> getTgssFeatureBit(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscInfoDO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscInfoDO.getTgssFeatureBit(mdnList, persisterTxn);
    }

    @Override
    public KnCorpInOutParamDTO cleanUpTGSSGrp(List<Integer> deletedGrpIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "cleanUpTGSSGrp(List<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");

        Set<String> mdns = new HashSet<String>();

        List<String> mdnList = null;
        KnCorpInOutParamDTO corpIODTO = new KnCorpInOutParamDTO();

        // get getMdnsForTGSSGrp for all groupids
        KnXDMCorpTGSSGrpDAO corpTGSSGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createTGSSGrpDAO(pttServerId);
        knLogger.debug(methodName,"Calling getMdnsForTGSSGrp for GrpIds",deletedGrpIds);
        for (Integer delGrpId : deletedGrpIds) {
            mdnList = corpTGSSGrpDAO.getMdnsForTGSSGrp(delGrpId, persisterTxn);
            if (mdnList != null && mdnList.size() >= 0) {
                for (String mdn : mdnList) {
                    mdns.add(mdn);
                }
            }
        }

        mdnList = new ArrayList<String>(mdns);
        // get getTGSSDocumentEtag for all mdns
        knLogger.debug(methodName, "call getTGSSDocumentEtag for input mdns::", KnGDPRTemplate.mdnList(mdnList));
        Map<String, Integer> mdnEtagMap = corpTGSSGrpDAO.getTGSSDocumentEtag(mdnList, persisterTxn);
        //  delete deleteTGSSGrps for all groupids
        knLogger.debug(methodName, "deleteTGSSGrps for groupIds::", deletedGrpIds);
        corpTGSSGrpDAO.deleteTGSSGrps(deletedGrpIds, persisterTxn);
        // update etag updateSubsTGSSGrpEtag for all mdns
        knLogger.debug(methodName, "update etag for mdns::", KnGDPRTemplate.mdnList(mdnList));
        for (Map.Entry<String, Integer> mdnEtag : mdnEtagMap.entrySet()) {
            int newEtag = mdnEtag.getValue() + 1;
            corpTGSSGrpDAO.updateSubsTGSSGrpEtag(mdnEtag.getKey(), mdnEtag.getValue() + 1, persisterTxn);
            mdnEtagMap.put(mdnEtag.getKey(),newEtag);
        }
        // set the   corpIODTO.setMdnTgssEtag(mdnTGSSEtag);
        corpIODTO.setMdnTgssEtag(mdnEtagMap);
        return corpIODTO;
    }

    @Override
    public KnCorpInOutParamDTO cleanUpTGSSGrpMdn(Integer deletedGrpId, List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "cleanUpTGSSGrpMdn(Integer,List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");

        KnCorpInOutParamDTO corpIODTO = new KnCorpInOutParamDTO();

        KnXDMCorpTGSSGrpDAO corpTGSSGrpDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createTGSSGrpDAO(pttServerId);
        // get getTGSSDocumentEtag for all mdns
        knLogger.debug(methodName, "call getTGSSDocumentEtag for input mdns::", KnGDPRTemplate.mdnList(mdnList));
        Map<String, Integer> mdnEtagMap = corpTGSSGrpDAO.getTGSSDocumentEtag(mdnList, persisterTxn);
        //  delete deleteTGSSGrps for all groupids
        knLogger.debug(methodName, "deleteTGSSGrps for groupIds::", deletedGrpId, "mdns:::::", KnGDPRTemplate.mdnList(mdnList));
        corpTGSSGrpDAO.deleteTGSSGrpsMdn(deletedGrpId, mdnList, persisterTxn);
        // update etag updateSubsTGSSGrpEtag for all mdns
        knLogger.debug(methodName, "update etag for mdns::", KnGDPRTemplate.mdnList(mdnList));
        for (Map.Entry<String, Integer> mdnEtag : mdnEtagMap.entrySet()) {
            int newEtag = mdnEtag.getValue() + 1;
            corpTGSSGrpDAO.updateSubsTGSSGrpEtag(mdnEtag.getKey(), newEtag, persisterTxn);
            mdnEtagMap.put(mdnEtag.getKey(),newEtag);
        }
        corpIODTO.setMdnTgssEtag(mdnEtagMap);
        return corpIODTO;
    }

    @Override
    public void updateSubsProfileLastUpdateTime(KnCorpSubscriberDTO corpSubscriberDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO xdmSubscriberInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        xdmSubscriberInfoDAO.updateSubsProfileLastUpdateTime(corpSubscriberDTO, persisterTxn);
    }

    public Map<Integer, Collection<KnCorpGroupMemberDTO>> getGrpSubsc(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO corpGroupMemList = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMCorpGroupMemberListDAO(pttServerId);
        return corpGroupMemList.getGrpSubsc(groupIds, persisterTxn);
    }

    @Override
    public void createOSMList(KnCorpOSMPersistDTO corpOSMPersistDTO,
                              KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "createOSMList";
        knLogger.debug(methodName,"Entry:");
        KnCorpOsmDAO corpOsmDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().corpOsmDAO(pttServerId);
        corpOsmDAO.createOsmList(corpOSMPersistDTO, persisterTxn);
    }

    @Override
    public List<KnXDMOSMInfoRequestDTO> getUniqueFlieldsOSMInfoList(String osmListId,
                                                                 KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getUniqueFlieldsOSMInfoList()";
        knLogger.debug(methodName,"Entry:");
        KnCorpOsmDAO corpOsmDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().corpOsmDAO(pttServerId);
        return corpOsmDAO.getUniqueFlieldsOSMInfoList(osmListId,false, persisterTxn);
    }

    @Override
    public void updateOSMList(KnCorpOSMPersistDTO corpOSMPersistDTO,
                              KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "updateOSMList";
        knLogger.debug(methodName,"Entry:");
        KnCorpOsmDAO corpOsmDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().corpOsmDAO(pttServerId);
        corpOsmDAO.updateOsmList(corpOSMPersistDTO, persisterTxn);
    }

    @Override
    public void deleteOSMList(String corpId,String osmListId,
                              KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "deleteOSMList";
        knLogger.debug(methodName,"Entry:");
        KnCorpOsmDAO corpOsmDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().corpOsmDAO(pttServerId);
        corpOsmDAO.deleteOsmList(corpId,osmListId, persisterTxn);
    }
    @Override
    public void deleteOSMListOnDeleteCorpProfile(int corpId,KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteOSMListOnDeleteCorpProfile(corpId, persisterTxn)";
        knLogger.debug(methodName," Entry -",corpId);
        KnCorpOsmDAO corpOsmDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().corpOsmDAO(pttServerId);
        corpOsmDAO.onDeleteCorpProfile(corpId, persisterTxn);
        knLogger.debug(methodName," Deleted OSM configuration successfully");
    }

    @Override
    public Map<Integer,Integer> getOSMListIdAndDefaultMap(String corpId, boolean readOnly,
                              KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getOSMListIdAndDefaultMap()";
        knLogger.debug(methodName,"Entry:");
        KnCorpOsmDAO corpOsmDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().corpOsmDAO(pttServerId);
        return corpOsmDAO.getOSMListIdAndDefaultMap(corpId, readOnly, persisterTxn);
    }

    @Override
    public Map<Integer,Integer> getOSMListIdAndDefaultMapByHierarchyId(String corpId, boolean readOnly,
                                                          KnPersisterTxn persisterTxn,String hierarchyId) throws KnDAOException{
        String methodName = "getOSMListIdAndDefaultMap()";
        knLogger.debug(methodName,"Entry:");
        KnCorpOsmDAO corpOsmDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().corpOsmDAO(pttServerId);
        return corpOsmDAO.getOSMListIdAndDefaultMapByHierarchyId(corpId, readOnly, persisterTxn,hierarchyId);
    }

    @Override
    public KnCorpOSMInfoListRespDTO getOSMListByCorp(String corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getOSMListByCorp(String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName,"Entry:");
        KnCorpOsmDAO corpOsmDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().corpOsmDAO(pttServerId);
        return corpOsmDAO.getOSMListByCorp(corpId, readOnly, persisterTxn);
    }

    public KnCorpOSMInfoListRespDTO getOSMListByCorpAndHierarchyId(String corpId, String hierarchyId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getOSMListByCorpAndHierarchyId(String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName,"Entry:");
        KnCorpOsmDAO corpOsmDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().corpOsmDAO(pttServerId);
        return corpOsmDAO.getOSMListByCorpAndHierarchyId(corpId,hierarchyId, readOnly, persisterTxn);
    }

    @Override
    public KnCorpOSMInfoListDetailsRespDTO getOSMListDetailsByListId(String corpId, String osmListId,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getOSMListDetailsByListId()";
        knLogger.debug(methodName,"Entry:");
        KnCorpOsmDAO corpOsmDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().corpOsmDAO(pttServerId);
        return corpOsmDAO.getOSMListDetailsByListId(corpId,osmListId,readOnly, persisterTxn);
    }

    @Override
    public void assignOSMIdToGroup(String corpId, String osmListId,Collection<String> assignedOSMIdToGroupIds,Collection<String> removedOSMIdFromGroupIds,String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "assignOSMIdToGroup()";
        knLogger.debug(methodName,"Entry:");
        KnCorpOsmDAO corpOsmDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().corpOsmDAO(pttServerId);
        corpOsmDAO.assignOSMIdToGroup(corpId,osmListId,assignedOSMIdToGroupIds,removedOSMIdFromGroupIds,xdmsHome, persisterTxn);
    }

    @Override
    public KnCorpOSMGroupListRespDTO getOSMGroupListByCorpAndOSMId(String corpId,Collection<String> OSMListIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getOSMGroupListByCorpAndOSMId(String, Collection<String>, boolean, KnPersisterTxn)";
        knLogger.debug(methodName,"Entry:");
        KnCorpOsmDAO corpOsmDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().corpOsmDAO(pttServerId);
        return corpOsmDAO.getOSMGroupListByCorpAndOSMId(corpId, OSMListIds, readOnly, persisterTxn);
    }

    @Override
    public Collection<KnSIPProxySvcConfigDTO> getSipProxySvcConfig(boolean readOnly,KnPersisterTxn persisterTxn) throws KnDAOException {
        KnSipProxySvcConfigDAO sipProxySvcConfigInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createSipProxySvcConfigInfoDAO(pttServerId);
        return sipProxySvcConfigInfoDAO.getSipProxySvcConfig(readOnly, persisterTxn);
    }

    @Override
    public Collection<KnAPNConfigDTO> getAPNProfileConfig(KnPersisterTxn persisterTxn) throws KnDAOException {
        KnAPNProfileDAO apnProfileInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createAPNProfileInfoDAO(pttServerId);
        return apnProfileInfoDAO.getAPNProfileConfig(persisterTxn);
    }

    @Override
    public void updateSubscriberMCSIds(KnSubsProfileDTO subscProfile, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        pocSubscInfoDAO.updateSubscriberMCSIds(subscProfile, persisterTxn);

    }
    @Override
    public void createUserProfile(String userProfileId,KnCorpUserProfileDTO userProfile, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createUserProfile()";
        knLogger.debug(methodName,"Entry userProfile:",userProfile);
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        knUserProfileDAO.createUserProfile(userProfileId, userProfile);
        knLogger.debug(methodName, "Exit");
    }

    @Override
    public void deleteUserProfile(String corpId, String userProfileId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteUserProfile()";
        knLogger.debug(methodName, "Entry: corpId -", corpId, " userProfileId -", userProfileId);
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        knUserProfileDAO.deleteUserProfile(corpId, userProfileId);
        knLogger.debug(methodName, "Exit");
    }

    @Override
    public void updateUserProfile(String corpId, String userProfileId, KnCorpModifyUserProfileDTO userProfile, KnPersisterTxn persisterTxn) throws KnDAOException, DocumentNotFoundException {
        String methodName = "updateUserProfile()";
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        knUserProfileDAO.updateUserProfile(corpId, userProfileId,userProfile);
        knLogger.debug(methodName, "Exit");
    }

    @Override
    public String getProfileName(int corpId, String userProfileName) throws KnDAOException {
        String methodName = "getProfileName(int,String)";
        knLogger.debug(methodName, "Entry: corpId -", corpId, "userProfielName -", userProfileName);
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        return knUserProfileDAO.getProfileName(corpId, userProfileName);
    }

    @Override
    public KnCorpUserProfileDTO getUserProfile(String corpId, String userProfileId) throws KnDAOException {
        String methodName = "getUserProfile()";
        knLogger.debug(methodName, "Entry: --->corpId -", corpId, "--->userProfileId -", userProfileId);
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        KnCorpUserProfileDTO userProfile = knUserProfileDAO.getUserProfile(corpId, userProfileId);
        knLogger.debug(methodName, "--->result from couchbase- ", userProfile);
        return userProfile;
    }
    @Override
    public KnCorpUserProfileDTO getUserProfile( String userProfileId) throws KnDAOException {
        String methodName = "getUserProfile()";
        knLogger.debug(methodName, "Entry:--->userProfileId -", userProfileId);
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        KnCorpUserProfileDTO userProfile = knUserProfileDAO.getUserProfile(userProfileId);
        knLogger.debug(methodName, "--->result from couchbase- ", userProfile);
        return userProfile;
    }

    @Override
    public List<KnCorpUserProfileDTO> getUserProfile( List<String> userProfileIds) throws KnDAOException {
        String methodName = "getUserProfile()";
        knLogger.debug(methodName, "Entry:--->userProfileIds -", userProfileIds);
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        List<KnCorpUserProfileDTO> userProfile = knUserProfileDAO.getUserProfile(userProfileIds);
        knLogger.debug(methodName, "--->result from couchbase- ", userProfile);
        return userProfile;
    }

    @Override
    public List<KnCorpUserProfileDTO> getUserProfile( List<String> userProfileIds, String fetchSize, String startIndex) throws KnDAOException {
        String methodName = "getUserProfile()";
        knLogger.debug(methodName, "Entry:--->userProfileIds -", userProfileIds,
                "  fetchSize -", fetchSize, "  startIndex -", startIndex);
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        List<KnCorpUserProfileDTO> userProfile = knUserProfileDAO.getUserProfile(userProfileIds, fetchSize, startIndex);
        knLogger.debug(methodName, "--->result from couchbase- ", userProfile);
        return userProfile;
    }

    @Override
    public Collection<KnCorpUserProfileDTO> getUserProfileListByCorpId(String corpId, String startIndex, String limit) throws KnDAOException {
        String methodName = "getUserProfileListByCorpId()";
        knLogger.debug(methodName, "Entry: --->corpId -", corpId, "--->startIndex -", startIndex,"--->limit -",limit);
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        Collection<KnCorpUserProfileDTO> userProfileList=knUserProfileDAO.getUserProfileListByCorpId(corpId, startIndex,limit);
        //return result;
        knLogger.debug(methodName, "--->result from couchbase- ", userProfileList);
        return userProfileList;
    }

    @Override
    public String getUserProfileIndexSeqence(int corpId) throws KnDAOException {
        String methodName = "getUserProfileIndexSeqence()";
        knLogger.debug(methodName, "Entry:");
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        String maxUPMindex = knUserProfileDAO.getUserProfileIndexSeqence(corpId);
        return maxUPMindex;
    }

    public Integer getMaxTotalCountByCorpId(String corpId) throws KnDAOException{
        String methodName = "getMaxTotalCountByCorpId()";
        knLogger.debug(methodName, "Entry:");
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        Integer maxTotalCount = knUserProfileDAO.getMaxTotalCountByCorpId(corpId);
        return maxTotalCount;
    }

    @Override
    public Collection<KnCorpUserProfileDTO> getUserProfileListByCorpAndHierarchyId(String corpId, String hierarchyId, String startIndex, String limit) throws KnDAOException {
        String methodName = "getUserProfileListByCorpAndHierarchyId()";
        knLogger.debug(methodName, "Entry: --->corpId -", corpId, "--->hierarchyId -", hierarchyId, "--->startIndex -", startIndex, "--->limit -", limit);
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        Collection<KnCorpUserProfileDTO> userProfileList = knUserProfileDAO.getUserProfileListByCorpAndHierarchyId(corpId, hierarchyId, startIndex, limit);
        knLogger.debug(methodName, "--->result from couchbase- ", userProfileList);
        return userProfileList;
    }

    @Override
    public Integer getMaxTotalCountByCorpAndHierarchyId(String corpId, String hierarchyId) throws KnDAOException {
        String methodName = "getMaxTotalCountByCorpAndHierarchyId()";
        knLogger.debug(methodName, "Entry:");
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        Integer maxTotalCount = knUserProfileDAO.getMaxTotalCountByCorpAndHierarchyId(corpId, hierarchyId);
        return maxTotalCount;
    }

    @Override
    public Set<String> getUserProfileIdsByHierarchyId(Collection<Integer> corpIds, String hierarchyId) throws KnDAOException {
        String methodName = "getUserProfileIdsByHierarchyId()";
        knLogger.debug(methodName, "Entry: corpIds -", corpIds, " hierarchyId -", hierarchyId);
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        Set<String> result = knUserProfileDAO.getUserProfileIdsByHierarchyId(corpIds, hierarchyId);
        knLogger.debug(methodName, "Exit: result count=", result.size(), " profileIds=", result);
        return result;
    }

    @Override
    public Collection<KnCorpUserProfileDTO> getUserProfileListByUserProfileNamePattern(String corpId, String userProfileNamePattern, String startIndex, String limit, String isCaseSensitiveSearch) throws KnDAOException {
        String methodName = "getUserProfileListByUserProfileNamePattern()";
        knLogger.debug(methodName, "Entry: --->corpId -", corpId, "--->startIndex -", startIndex,"--->limit -",limit);
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        Collection<KnCorpUserProfileDTO> userProfileList = knUserProfileDAO.getUserProfileListByUserProfileNamePattern(corpId, userProfileNamePattern, startIndex, limit, isCaseSensitiveSearch);
        knLogger.debug(methodName, "--->result from couchbase- ", userProfileList);
        return userProfileList;
    }

    @Override
    public Collection<KnCorpUserProfileDTO> getUserProfileListForNamePattern(String corpId, String userProfileNamePattern, String startIndex, String limit, String isCaseSensitiveSearch, List<String> userProfileIds) throws KnDAOException {
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        Collection<KnCorpUserProfileDTO> userProfileList = knUserProfileDAO.getUserProfileListForNamePattern(corpId, userProfileNamePattern, startIndex, limit, isCaseSensitiveSearch, userProfileIds);
        return userProfileList;
    }

    @Override
    public Collection<KnCorpUserProfileDTO> getUserProfileListForNamePattern(String corpId, String userProfileNamePattern, String isCaseSensitiveSearch, List<String> userProfileIds) throws KnDAOException {
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        Collection<KnCorpUserProfileDTO> userProfileList = knUserProfileDAO.getUserProfileListForNamePattern(corpId, userProfileNamePattern, isCaseSensitiveSearch, userProfileIds);
        return userProfileList;
    }

    @Override
    public Integer getUserProfileCountByUserProfileNamePatternAndUserprofileIds(String corpId, String userProfileNamePattern, String isCaseSensitiveSearch, List<String> userProfileIds) throws KnDAOException {
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        Integer result = knUserProfileDAO.getUserProfileCountByUserProfileNamePatternAndUserprofileIds(corpId, userProfileNamePattern, isCaseSensitiveSearch, userProfileIds);
        return result;
    }

    @Override
    public List<KnCorpUserProfileDTO> getUserProfileListByUserProfileNamePatternSharedCorp(List<String> userProfileIds, String userProfileNamePattern, String startIndex, String limit, String isCaseSensitiveSearch) throws KnDAOException {
        String methodName = "getUserProfileListByUserProfileNamePatternSharedCorp()";
        knLogger.debug(methodName, "Entry: --->userProfileIds -", userProfileIds, "--->startIndex -", startIndex,"--->limit -",limit);
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        List<KnCorpUserProfileDTO> userProfileList = knUserProfileDAO.getUserProfileListByUserProfileNamePatternSharedCorp(userProfileIds, userProfileNamePattern, startIndex, limit, isCaseSensitiveSearch);
        knLogger.debug(methodName, "--->result from couchbase- ", userProfileList);
        return userProfileList;
    }

    @Override
    public Integer getMaxTotalCountByCorpIdAndProfileNamePattern(String corpId,String userProfileNamePattern) throws KnDAOException{
        String methodName = "getMaxTotalCountByCorpId()";
        knLogger.debug(methodName, "Entry:");
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        Integer maxTotalCount = knUserProfileDAO.getMaxTotalCountByCorpIdAndProfileNamePattern(corpId, userProfileNamePattern);
        return maxTotalCount;
    }


    @Override
    public  Map<Integer, Integer> getSubscriberUserProfileList(String mcId, int corpId,
                                                                            KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberUserProfileList()";
        knLogger.debug(methodName, "Entry :");
        KnXDMSubscriberInfoDAO subscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        Map<Integer, Integer> userProfileList = subscDAO.getSubscriberUserProfileList(mcId, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return userProfileList;
    }
    @Override
    public  Map<String,Integer> getSubscriberUserProfileList(String mcId, boolean readOnly,
                                                                            KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getSubscriberUserProfileList(String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMSubscriberInfoDAO subscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        Map<String,Integer> userProfileList = subscDAO.getSubscriberUserProfileList(mcId, readOnly, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return userProfileList;
    }


    @Override
    public Collection<KnSubscriberUserProfileDTO> getUserProfileListByUserProfileIndex(String corpId,
                                                                                 List<Integer> userProfileIndex) throws KnDAOException {
        String methodName = "getUserProfileListByUserProfileIndex()";
        knLogger.debug(methodName, "Entry: --->corpId -", corpId, "--->userProfileIndex -", userProfileIndex);
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        Collection<KnSubscriberUserProfileDTO> userProfileList = knUserProfileDAO.getUserProfileListByUserProfileIndex(corpId,
                userProfileIndex);
        knLogger.debug(methodName, "--->result from couchbase- ", userProfileList);
        return userProfileList;
    }

    @Override
    public List<String> getProfileMdnByBaseMdn(String baseMdn,KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileMdnByBaseMdn()";
        knLogger.debug(methodName, "Entry baseMdn:",KnGDPRTemplate.mdn(baseMdn));
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getProfileMdnByBaseMdn(baseMdn,persisterTxn);
    }

    @Override
    public List<String> getProfileMdnByBaseMdns(List<String> baseMdns,KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileMdnByBaseMdns()";
        knLogger.debug(methodName, "Entry baseMdns : ",KnGDPRTemplate.mdnList(baseMdns));
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getProfileMdnByBaseMdns(baseMdns,persisterTxn);
    }

    @Override
    public Collection<KnMDNInfoDto> getUserProfileSubscriberList(int corpId, String userProfileId, int startIndex, int fetchSize, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUserProfileSubscriberList(String, String, int, boolean, int)";
        knLogger.debug(methodName, "Entry: --->corpId -", corpId, "--->userProfileId -", userProfileId);
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        Collection<KnMDNInfoDto> mdnList = pocSubscInfoDAO.getUserProfileSubscriberList(corpId, userProfileId, startIndex, fetchSize, readOnly, persisterTxn);
        knLogger.debug(methodName, "--->mdnList - ", mdnList);
        return mdnList;
    }

    @Override
    public void updateDefaultProfile(String corpId, String profileId, List<String> mdns, int defaultProfile, KnPersisterTxn persisterTxn) throws KnDAOException {

        String methodName = "updateDefaultProfile(String,String,String,int)";
        knLogger.debug(methodName, "Entry: --->corpId -", corpId, "--->userProfileId -", profileId,"mdns-->",mdns == null ? mdns : KnGDPRTemplate.mdnList(mdns),"defaultProfile--> ",defaultProfile);
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        pocSubscInfoDAO.updateDefaultProfile(corpId,profileId,mdns,defaultProfile,persisterTxn);
        knLogger.debug(methodName,"EXIT:-");
    }


    @Override
    public List<String> getProfileMdnByUPId(String corpId,String userProfileId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getUserProfileSubscriberList(String,String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "Entry: --->-", "--->userProfileId -", userProfileId);
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        List<String> mdnList = pocSubscInfoDAO.getProfileMdnByUPId(corpId, userProfileId, readOnly, persisterTxn);
        knLogger.debug(methodName, "--->mdnList - ", KnGDPRTemplate.mdnList(mdnList));
        return mdnList;
    }

    public String getDefaultProfileMdnByMdn(String mdn, int isDefaultProfile, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getDefaultProfileMdnByMdn(String,int,persisterTxn)";
        knLogger.debug(methodName, "Entry: --->-", "mdn -->",KnGDPRTemplate.mdn(mdn),"isDefaultProfile-->",isDefaultProfile);
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        String defaultProfileMdn = pocSubscInfoDAO.getDefaultProfileMdnByMdn(mdn,isDefaultProfile,persisterTxn);
        knLogger.debug(methodName, "--->defaultProfileMdn - ", KnGDPRTemplate.mdn(defaultProfileMdn));
        return defaultProfileMdn;
    }

    @Override
    public String getProfileMdnByMdnUPID(String profileId, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileMdnByMdnUPID(String,String,persisterTxn)";
        knLogger.debug(methodName, "Entry: --->-","profileId ->",profileId, "mdn -->",KnGDPRTemplate.mdn(mdn));
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        String defaultProfileMdn = pocSubscInfoDAO.getProfileMdnByMdnUPID(profileId,mdn,persisterTxn);
        knLogger.debug(methodName, "--->defaultProfileMdn - ", KnGDPRTemplate.mdn(defaultProfileMdn));
        return defaultProfileMdn;
    }


    @Override
    public Set<String> getUniqueMcpttIds(Collection<String> mdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUniqueMcpttIds(Collection<String>,String,persisterTxn)";
        knLogger.debug(methodName, "Entry: --->-","mdns ->",KnGDPRTemplate.mdnList(mdns));
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        Set<String> uniqueMcpttIds = pocSubscInfoDAO.getUniqueMcpttIds(mdns, persisterTxn);
        knLogger.debug(methodName, "--->uniqueMcpttIds - ", KnGDPRTemplate.mcPttIdSetList(uniqueMcpttIds));
        return uniqueMcpttIds;
    }

    @Override
    public Map<Integer,Integer> getUpIndexCountMap(Collection<Integer> userprofileIndexes, Integer corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException{
        final String methodName = "getUpIndexCountMap(String, Integer, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnXDMSubscriberInfoDAO subscDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        Map<Integer,Integer> countMap = subscDAO.getUpIndexCountMap(userprofileIndexes, corpId, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return countMap;
    }

    @Override
    public Map<String, KnCorpGroupMemberDTO> getGroupMembersListForWcsrOrCatUi(int groupId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO grpMemListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return grpMemListDAO.getGroupMembersListForWcsrOrCatUi(groupId, readOnly, persisterTxn);
    }

    @Override
    public List<String> getRealMdns(List<String> mdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO grpMemListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return grpMemListDAO.getRealMdns(mdns, readOnly, persisterTxn);
    }

    @Override
    public Map<String, Integer> getUserProfileIdFromSublistIds(String corpId, Collection<Integer> subListId) throws KnDAOException {
        String methodName = "getUserProfileIdFromSublistIds(String, List<Integer>)";
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        return knUserProfileDAO.getUserProfileIdFromSublistIds(corpId, subListId);
    }

    @Override
    public Map<String, Collection<String>> getUserProfileIdFromGroupIds(Collection<Integer> corpIds, Collection<Integer> groupIds) throws KnDAOException {
        String methodName = "getUserProfileIdFromGroupIds(String, List<Integer>)";
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        return knUserProfileDAO.getUserProfileIdFromGroupIds(corpIds, groupIds);
    }

    @Override
    public Map<String, Collection<KnDocChangeListDTO>> updateSubsTS(Set<String> mdnList,String exists, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMDirectoryDAO directoryDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMDirectoryDAO(pttServerId);
        return directoryDAO.updateSubsTS(mdnList,exists, persisterTxn);
    }

    @Override
    public Map<String, Collection<KnDocChangeListDTO>> updateSubsTSandDirecEtag(Set<String> mdnList,String exists, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMDirectoryDAO directoryDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMDirectoryDAO(pttServerId);
        return directoryDAO.updateSubsTSandDirecEtag(mdnList,exists, persisterTxn);
    }

    @Override
    public Map<String,String> getProfileMdnsByCorpId(String corpId,  KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getProfileMdnsByCorpId()";
        knLogger.debug(methodName, "Entry corpId:",corpId);
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getProfileMdnsByCorpId(corpId,persisterTxn);
    }

    @Override
    public Map<String, List<String>> getMapOfProfileMdnByBaseMdn(List<String> baseMdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getMapOfProfileMdnByBaseMdn(baseMdnList, readOnly, persisterTxn);
    }

    @Override
    public  List<KnCorpSubscriberDTO> getProfileMdnInfoByUPId(String corpId,String userProfileId, KnPersisterTxn persisterTxn) throws KnDAOException{
        String methodName = "getProfileMdnInfoByUPId(String,String,KnPersisterTxn)";
        knLogger.debug(methodName, "Entry: --->-", "--->userProfileId -", userProfileId);
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        List<KnCorpSubscriberDTO> subbscriberInfoList = pocSubscInfoDAO.getProfileMdnInfoByUPId(corpId,userProfileId,persisterTxn);
        knLogger.debug(methodName, "--->subbscriberInfoList - ", subbscriberInfoList);
        return subbscriberInfoList;
    }

    @Override
    public void updateImpactedTuPerms(int corpId,Collection<String> addTuMdns,Collection<String> removeTuMdns, Collection<String> upmIds) throws KnDAOException {
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        knUserProfileDAO.updateImpactedTuPerms(corpId,addTuMdns,removeTuMdns, upmIds);
    }

    @Override
    public void insertProfileGroupInfo(String userProfileId, Set<KnCorpGroupListInfoDTO> groupList,KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "insertProfileGroupInfo(String,Set<KnCorpGroupListInfoDTO>,String,KnPersisterTxn)";
        knLogger.debug(methodName, "userProfileId -", userProfileId);
        KnXDMProfilGroupInfoDAO profilGroupInfoDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMProfilGroupInfoDAO(pttServerId);
        profilGroupInfoDao.insertProfileGroupInfo(userProfileId, groupList, persisterTxn);
        knLogger.info(methodName, "EXIT:-");
    }
    @Override
    public Set<KnCorpGroupListInfoDTO> retriveGroupProfileInfoByProfileId(String userProfileId ,boolean readOnly,KnPersisterTxn persisterTxn) throws KnDBPersistenceException, KnDBConnectionException, KnDAOException {
        String methodName = "retriveGroupProfileInfoByProfileId(String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "userProfileId -", userProfileId);
        KnXDMProfilGroupInfoDAO profilGroupInfoDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMProfilGroupInfoDAO(pttServerId);
        return profilGroupInfoDao.retriveGroupProfileInfoByProfileId(userProfileId,readOnly, persisterTxn);
    }
    @Override
    public Map<String,KnCorpGroupListInfoDTO> retriveGroupProfileInfoByGroupId(Integer groupId ,KnPersisterTxn persisterTxn) throws KnDBPersistenceException, KnDBConnectionException, KnDAOException {
        String methodName = "retriveGroupProfileInfoByGroupId(Integer,KnPersisterTxn)";
        knLogger.debug(methodName, "groupId -", groupId);
        KnXDMProfilGroupInfoDAO profilGroupInfoDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMProfilGroupInfoDAO(pttServerId);
        return profilGroupInfoDao.retriveGroupProfileInfoByGroupId(groupId, persisterTxn);
    }

    @Override
    public  Collection<KnCorpGroupMemberDTO>  groupProfileInfoByGroupId(Integer groupId ,KnPersisterTxn persisterTxn) throws KnDBPersistenceException, KnDBConnectionException, KnDAOException {
        String methodName = "groupProfileInfoByGroupId(Integer,KnPersisterTxn)";
        KnXDMProfilGroupInfoDAO profilGroupInfoDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMProfilGroupInfoDAO(pttServerId);
        return profilGroupInfoDao.groupProfileInfoByGroupId(groupId, persisterTxn);
    }


    @Override
    public void deleteProfileGroupInfo(String userProfileId, List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteProfileGroupInfo(String,List<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "userProfileId -", userProfileId);
        KnXDMProfilGroupInfoDAO profilGroupInfoDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMProfilGroupInfoDAO(pttServerId);
        profilGroupInfoDao.deleteProfileGroupInfo(userProfileId, groupIds, persisterTxn);
        knLogger.debug(methodName,"EXIT:-");
    }

    @Override
    public void deleteProfileGroupInfoByGroupId(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteProfileGroupInfoByGroupId(int, KnPersisterTxn)";
        knLogger.debug(methodName, "groupId -", groupId);
        KnXDMProfilGroupInfoDAO profilGroupInfoDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMProfilGroupInfoDAO(pttServerId);
        profilGroupInfoDao.deleteProfileGroupInfoByGroupId(groupId, persisterTxn);
        knLogger.debug(methodName,"EXIT:-");
    }

    @Override
    public void deleteProfileGroupInfoByProfileId(String profileId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteProfileGroupInfoByGroupId(String, KnPersisterTxn)";
        knLogger.debug(methodName, "profileId", profileId);
        KnXDMProfilGroupInfoDAO profilGroupInfoDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMProfilGroupInfoDAO(pttServerId);
        profilGroupInfoDao.deleteProfileGroupInfoByProfileId(profileId, persisterTxn);
        knLogger.debug(methodName,"EXIT:-");
    }

    @Override
    public void deleteProfileSharedList(String profileId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deleteProfileSharedList(String, KnPersisterTxn)";
        knLogger.debug(methodName, "profileId", profileId);
        KnXDMProfilGroupInfoDAO profilGroupInfoDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMProfilGroupInfoDAO(pttServerId);
        profilGroupInfoDao.deleteProfileSharedList(profileId, persisterTxn);
        knLogger.debug(methodName,"EXIT:-");
    }

    @Override
    public Collection<Integer> getAllSublistForGroupIds(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupListRefDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupListRefDAO(pttServerId);
        return corpGroupInfoDAO.getAllSublistForGroupIds(groupIdsList, persisterTxn);
    }

    @Override
    public Set<String> getBaseMdnByProfileMdns(List<String> profileMdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getBaseMdnByProfileMdns(profileMdns, readOnly, persisterTxn);
    }
    
    @Override
    public Map<String, String> getProfileMdnBaseMdnMap(List<String> profileMdns, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getProfileMdnBaseMdnMap(profileMdns, readOnly, persisterTxn);
    }


    public void createSublist(List<KnCorpSublistDTO> sublistDTOList, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "createSublist()";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        corpListDAO.insertCorpListInfo(sublistDTOList, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    @Override
    public List<String> getExistingGroupName(List<String> grpNameList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupInfoDAO corpGrpInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpGroupInfoDAO(pttServerId);
        return corpGrpInfoDAO.getExistingGroupName(grpNameList , corpId, persisterTxn);
    }

    @Override
    public void createBulkGroupInfoDetails(List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupInfoDAO corpGrpInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpGroupInfoDAO(pttServerId);
        corpGrpInfoDAO.createBulkGroupInfoDetails(groupInfoPersistDTOList, persisterTxn);
    }

    @Override
    public void addBulkSublistsToGroup(List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupListRefDAO groupListRefDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupListRefDAO(pttServerId);
        groupListRefDAO.addBulkSublistsToGroup(groupInfoPersistDTOList, persisterTxn);
    }

    @Override
    public KnCorpGroupProfileInfo getGroupProfileDetailById(Integer profileId, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        return groupProfileDAO.getGroupProfileDetailById(profileId, corpId, readOnly, persisterTxn);
    }

    @Override
    public KnCorpGroupProfileInfo getGroupProfileDetailByName(String profileName, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        return groupProfileDAO.getGroupProfileDetailByName(profileName, corpId, readOnly, persisterTxn);
    }

    @Override
    public KnCorpGroupProfileInfo getGroupProfileDetailByIdAndHierarchyId(Integer profileId, int corpId, String hierarchyId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        return groupProfileDAO.getGroupProfileDetailByIdAndHierarchyId(profileId, corpId, hierarchyId, readOnly, persisterTxn);
    }

    @Override
    public KnCorpGroupProfileInfo getGroupProfileDetailByNameAndHierarchyId(String profileName, int corpId, String hierarchyId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        return groupProfileDAO.getGroupProfileDetailByNameAndHierarchyId(profileName, corpId, hierarchyId, readOnly, persisterTxn);
    }

    @Override
    public List<KnCorpGroupInfoPersistDTO> selectProfileGroupList(KnIPCorpGroupProfileDTO groupProfileDTO, int
            maxMemPerGroup, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupInfoDAO corpGrpInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpGroupInfoDAO(pttServerId);
        return corpGrpInfoDAO.selectProfileGroupList(groupProfileDTO , maxMemPerGroup, readOnly, persisterTxn);
    }


    @Override
    public void createGroupProfile(KnCorpGroupProfilePersistDTO groupProfilePersistDTO, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        groupProfileDAO.createGroupProfile(groupProfilePersistDTO, persisterTxn);

    }


    @Override
    public List<KnCorpGroupProfileInfo> getGroupProfileList(int corpId,Integer startIndex, Integer fetchSize, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        return groupProfileDAO.getGroupProfileList(corpId, startIndex, fetchSize, persisterTxn);
    }

    @Override
    public List<KnCorpGroupProfileInfo> getGroupProfileListByHierarchyId(int corpId, Integer startIndex, Integer fetchSize, KnPersisterTxn persisterTxn, String hierarchyId) throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        return groupProfileDAO.getGroupProfileListByHierarchyId(corpId, startIndex, fetchSize, persisterTxn,hierarchyId);
    }


    @Override
    public Map<String, Integer> selectGroupCountByGroupProfileId(List<String> groupProfileIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupInfoDAO corpGrpInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpGroupInfoDAO(pttServerId);
        return corpGrpInfoDAO.selectGroupCountByGroupProfileId(groupProfileIds , readOnly, persisterTxn);
    }


    @Override
    public int getGroupProfileCountByCorpId(int corpId, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        return groupProfileDAO.getGroupProfileCountByCorpId(corpId, persisterTxn);
    }

    @Override
    public int getGroupProfileCountByCorpIdByHierarchyId(int corpId, String xdmsHome, KnPersisterTxn persisterTxn, String hierarchyId) throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        return groupProfileDAO.getGroupProfileCountByCorpIdByHierarchyId(corpId,persisterTxn,hierarchyId);
    }

    @Override
    public List<KnCorpGroupProfileInfo> searchGroupProfileByNameAndType(int corpId, Integer startIndex, Integer fetchSize, Integer grpType, String grpProfileName,
                                                                        KnPersisterTxn persisterTxn, String hierarchyId) throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        return groupProfileDAO.searchGroupProfileByNameAndType(corpId, startIndex, fetchSize, grpType,grpProfileName, persisterTxn, hierarchyId);
    }

    @Override
    public List<KnCorpGroupProfileInfo> searchGroupProfileByGpType(int corpId, Integer startIndex, Integer fetchSize, Integer grpType,
                                                                   KnPersisterTxn persisterTxn, String hierarchyId) throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        return groupProfileDAO.searchGroupProfileByGpType(corpId, startIndex, fetchSize, grpType, persisterTxn, hierarchyId);
    }

    @Override
    public List<KnCorpGroupProfileInfo> searchGroupProfileByProfileName(int corpId, Integer startIndex, Integer fetchSize, String grpProfileName,
                                                                        KnPersisterTxn persisterTxn, String hierarchyId) throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        return groupProfileDAO.searchGroupProfileByProfileName(corpId, startIndex, fetchSize, grpProfileName, persisterTxn, hierarchyId);
    }

    @Override
    public Map<String, Integer> getSubsScrGroupCount(List<String> mdns, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getSubsScrGroupCount(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdns - ", mdns == null ?  mdns : KnGDPRTemplate.mdnList(mdns));

        KnXDMCorpGroupMemberListDAO groupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMCorpGroupMemberListDAO(pttServerId);
        Map<String, Integer> SubsScrGroupCountMap = groupMemberListDAO.getSubsScrGroupCount(mdns,persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return SubsScrGroupCountMap;
    }

    @Override
    public Map<String, Integer> getSubsScrGroupCount(List<String> mdns, KnPersisterTxn persisterTxn, int corpId)
            throws KnDAOException {
        final String methodName = "getSubsScrGroupCount(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : mdns - ", mdns == null ?  mdns : KnGDPRTemplate.mdnList(mdns));

        KnXDMCorpGroupMemberListDAO groupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMCorpGroupMemberListDAO(pttServerId);
        Map<String, Integer> SubsScrGroupCountMap = groupMemberListDAO.getSubsScrGroupCount(mdns,persisterTxn, corpId);
        knLogger.debug(methodName, "EXIT");
        return SubsScrGroupCountMap;
    }

    @Override
    public Map<String, Integer> getSubsScrGroupCountExceptABDG(List<String> mdns, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMCorpGroupMemberListDAO(pttServerId);
        Map<String, Integer> SubsScrGroupCountMap = groupMemberListDAO.getSubsScrGroupCountExceptABDG(mdns,persisterTxn);
        return SubsScrGroupCountMap;
    }

    @Override
    public Map<String, Integer> getSubsScrGroupCountExceptABDG(List<String> mdns, KnPersisterTxn persisterTxn, int corpId)
            throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMCorpGroupMemberListDAO(pttServerId);
        Map<String, Integer> SubsScrGroupCountMap = groupMemberListDAO.getSubsScrGroupCountExceptABDG(mdns,persisterTxn, corpId);
        return SubsScrGroupCountMap;
    }

    @Override
    public void updateGroupProfile(KnCorpGroupProfilePersistDTO groupProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        groupProfileDAO.updateGroupProfile(groupProfilePersistDTO, persisterTxn);
    }


    @Override
    public Map<String, List<Integer>> getSubscriberDistGroupList(Set<Integer> groupList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupDistInfoDAO groupDistInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupDistInfoDAO(pttServerId);
        return groupDistInfoDAO.getSubscriberDistGroupList(groupList, persisterTxn);
    }

    @Override
    public void updateGroupInfoProperties(Map<Integer, KnCorpGroupDTO> groupEtagList, KnCorpGroupProfilePersistDTO
            groupProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        groupInfoDAO.updateGroupInfoProperties(groupEtagList, groupProfilePersistDTO, persisterTxn);
    }

    @Override
    public void deleteGroupProfile(Integer profileId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        groupProfileDAO.deleteGroupProfile(profileId, persisterTxn);
    }

    @Override
    public void deleteBulkProfileGroupInfo(List<Integer> groupIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMProfilGroupInfoDAO profilGroupInfoDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMProfilGroupInfoDAO(pttServerId);
        profilGroupInfoDao.deleteBulkProfileGroupInfo(groupIdList, persisterTxn);
    }
    @Override
    public Set<Integer> getGroupProfileDetailByOsmListId(int corpId, int osmListId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        return groupProfileDAO.getGroupProfileDetailByOsmListId(corpId, osmListId, persisterTxn);
    }

    @Override
    public void deleteOSMListFromProfile(int osmListId,int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        groupProfileDAO.deleteOSMListFromProfile(osmListId,corpId,persisterTxn);
    }

    @Override
    public int getGroupProfileCountByGrpType(int corpId, Integer grpType, KnPersisterTxn persisterTxn, String hierarchyId) throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        return groupProfileDAO.getGroupProfileCountByGrpType(corpId,grpType,persisterTxn, hierarchyId);
    }

    @Override
    public int getGroupProfileCountByName(int corpId, String grpProfileName, KnPersisterTxn persisterTxn, String hierarchyId) throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        return groupProfileDAO.getGroupProfileCountByName(corpId,grpProfileName,persisterTxn, hierarchyId);
    }

    @Override
    public void updateProfileMdnDetails(KnCorpSubscriberDTO subsProfilePersistDTO
            , Map<String, String> mdnActivsFsMap,  Map<String, KnCorpSubscriberDTO> mdnUpmFsMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        subscriberTable.updateProfileMdnDetails(subsProfilePersistDTO, mdnActivsFsMap, mdnUpmFsMap, persisterTxn);
    }

    @Override
    public Map<String, KnCorpSubscriberDTO> getProfileMdnAndUpmfsByBaseMdn(String baseMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.getProfileMdnAndUpmfsByBaseMdn(baseMdn, persisterTxn);
    }

    @Override
    public void createGroupProfileSharedCorpInfo(KnCorpGroupProfilePersistDTO groupProfilePersistDTO, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupProfileSharedListDAO groupProfileSharedListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileSharedListDAO(pttServerId);
        groupProfileSharedListDAO.createGroupProfileSharedCorpInfo(groupProfilePersistDTO,persisterTxn);

    }

    @Override
    public Map<String, Integer> getCorpIdMap(Set<Integer> corpIds,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpInfoDAO corpInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpInfoDAO(pttServerId);
        return corpInfoDAO.getCorpIdMap(corpIds,readOnly, persisterTxn);
    }

    @Override
    public Map<String, Integer> getCorpIdMapByExtCorpIds(Set<String> extCorpIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpInfoDAO corpInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpInfoDAO(pttServerId);
        return corpInfoDAO.getCorpIdMapByExtCorpIds(extCorpIds, readOnly, persisterTxn);
    }

    @Override
    public List<KnCorpSharedCorpInfo> selectGroupProfileSharedCorpInfo(int ownedCorpId, Integer grpProfileId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupProfileSharedListDAO groupProfileSharedListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileSharedListDAO(pttServerId);
        return groupProfileSharedListDAO.selectGroupProfileSharedCorpInfo(ownedCorpId, grpProfileId, readOnly, persisterTxn);
    }

    @Override
    public void createGroupSharedCorpInfo(List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupSharedListDAO grpSharedListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupSharedListDAO(pttServerId);
        grpSharedListDAO.createGroupSharedCorpInfo(groupInfoPersistDTOList,persisterTxn);
    }

    @Override
    public List<KnCorpSharedCorpInfo> selectGroupSharedCorpInfo(int ownedCorpId, int groupId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupSharedListDAO grpSharedListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupSharedListDAO(pttServerId);
        return grpSharedListDAO.selectGroupSharedCorpInfo(ownedCorpId, groupId, readOnly, persisterTxn);
    }

    @Override
    public Map<Integer, List<KnCorpSharedCorpInfo>> selectGroupProfileSharedCorpInfo(int ownedCorpId, Collection<Integer> grpProfileIds,
                                                                                     KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupProfileSharedListDAO groupProfileSharedListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileSharedListDAO(pttServerId);
        return groupProfileSharedListDAO.selectGroupProfileSharedCorpInfo(ownedCorpId,grpProfileIds,persisterTxn);

    }

    @Override
    public Map<Integer, List<KnCorpSharedCorpInfo>> selectGroupSharedCorpInfo(int ownedCorpId, Collection<Integer> groupIds, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnCorpGroupSharedListDAO grpSharedListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupSharedListDAO(pttServerId);
        return grpSharedListDAO.selectGroupSharedCorpInfo(ownedCorpId, groupIds, readOnly, persisterTxn);
    }

    @Override
    public Map<Integer, List<KnCorpSharedCorpInfo>> selectGroupSharedCorpInfoByGroupId(Collection<Integer> groupIds,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupSharedListDAO grpSharedListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupSharedListDAO(pttServerId);
        return grpSharedListDAO.selectGroupSharedCorpInfoByGroupId(groupIds,readOnly,persisterTxn);
    }

    @Override
    public Map<Integer,List<KnCorpSharedCorpInfo>> selectGroupSharedCorpInfoBySharedCorpId(int sharedCorpId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException  {
        KnCorpGroupSharedListDAO grpSharedListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupSharedListDAO(pttServerId);
        return grpSharedListDAO.selectGroupSharedCorpInfoBySharedCorpId(sharedCorpId, readOnly, persisterTxn);
    }

    /**
     * Method to delete list of group ids from shared group info.
     * @param groupIds
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteSharedGroups(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupSharedListDAO grpSharedListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupSharedListDAO(pttServerId);
        grpSharedListDAO.deleteSharedGroups(groupIds,persisterTxn);
    }

    @Override
    public void deleteGroupProfileSharedInfo(Integer profileId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupProfileSharedListDAO groupProfileSharedListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileSharedListDAO(pttServerId);
        groupProfileSharedListDAO.deleteGroupProfileSharedInfo(profileId,persisterTxn);
    }

    @Override
    public void deleteGroupProfileSharedInfoByOwnedCorpId(Integer ownedCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupProfileSharedListDAO groupProfileSharedListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileSharedListDAO(pttServerId);
        groupProfileSharedListDAO.deleteGroupProfileSharedInfoByOwnedCorpId(ownedCorpId,persisterTxn);
    }

    @Override
    public void deleteGroupProfileByCorpId(Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupProfileDAO groupProfileDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupProfileDAO(pttServerId);
        groupProfileDAO.deleteGroupProfileByCorpId(corpId,persisterTxn);
    }

    @Override
    public List<KnCORPGroupStatsRespDTO> getGroupStats(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpStatsDAO groupStatsDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpStatsDAODAO(pttServerId);
        return groupStatsDAO.getGroupStats(corpId, readOnly, persisterTxn);
    }

    @Override
    public void deleteProfileGroupInfoByProfileIds(List<String> profileIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMProfilGroupInfoDAO profilGroupInfoDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMProfilGroupInfoDAO(pttServerId);
        profilGroupInfoDao.deleteProfileGroupInfoByProfileIds(profileIds, persisterTxn);
    }

    @Override
    public void deleteSharedGroupsByOwnedCorp(Integer ownedCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupSharedListDAO grpSharedListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupSharedListDAO(pttServerId);
        grpSharedListDAO.deleteSharedGroupsByOwnedCorp(ownedCorpId,persisterTxn);
    }

    @Override
    public void deleteUserProfilesByCorpId(String corpId,KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        knUserProfileDAO.deleteUserProfilesByCorpId(corpId);
    }

    @Override
    public Collection<String> getUserProfileIdsByCorpId(Integer corpId,KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        Collection<String> userProfileList=knUserProfileDAO.getUserProfileIdsByCorpId(corpId);
        return userProfileList;
    }

    @Override
    public void modifyGroup_GrpSharedFlag(Map<Integer, Integer> groupIdSharedFlagMap, String pttServerId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.modifyGroup_GrpSharedFlag(groupIdSharedFlagMap, persisterTxn);
    }

    @Override
    public List<KnCorpSharedCorpInfo> selectGroupSharedCorpInfoByOwnedCorpId(int ownedCorpId, KnPersisterTxn persisterTxn)
            throws KnDAOException  {
        KnCorpGroupSharedListDAO grpSharedListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupSharedListDAO(pttServerId);
        return grpSharedListDAO.selectGroupSharedCorpInfoByOwnedCorpId(ownedCorpId,persisterTxn);
    }

    @Override
    public List<Integer> getGroupIdsByMemberAndGroupType(String mdn,Integer groupType, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getGroupIdsByMemberAndGroupType(mdn, groupType, persisterTxn);
    }

    @Override
    public Map<String, Integer> getAndDeleteGroupIdsFromUserProfile(Collection<Integer> groupIds) throws KnDAOException {
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        return knUserProfileDAO.getAndDeleteGroupIdsFromUserProfile(groupIds);
    }

    @Override
    public Collection<KnCorpGroupInfoPersistDTO> selectSubsGroupList(String subsMdn, int maxMemPerGroup, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "selectSubsGroupList(String, int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : subsMdn - ", KnGDPRTemplate.mdn(subsMdn), ", maxMemPerGroup - ", maxMemPerGroup);
        String query = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = queryMapper.getQuery(GET_SUBS_GROUP_LIST);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, subsMdn);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ", query);
            Collection<KnCorpGroupInfoPersistDTO> groupList = new ArrayList<KnCorpGroupInfoPersistDTO>();
            while (rs.next()) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(rs.getInt(1));
                //multilingual revert change
                if (rs.getString(2) != null) {
                    groupPersistDTO.setGroupDisplayName(new String(rs.getString(2).trim().getBytes("8859_1"), "UTF-8"));
                }
                int memCount = rs.getInt(3);
                if (memCount > maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(GREATER_THAN_LIMIT);
                } else if (memCount == maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(EQUAL_TO_LIMIT);
                } else if (memCount < MIN_MEMBER_LIMIT) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(LESS_THAN_MIN_LIMIT);
                } else if (memCount < maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(LESS_THAN_LIMIT);
                }
                /*int broadcaster = rs.getInt(7);
                if (broadcaster == KnPersisterConstants.IS_BROADCASTER) {
                    List<KnCorpGroupMemberDTO> mdnList = new ArrayList<KnCorpGroupMemberDTO>();
                    KnCorpGroupMemberDTO memberDTO = new KnCorpGroupMemberDTO();
                    memberDTO.setMdn(subsMdn);
                    memberDTO.setBroadcaster(broadcaster);
                    mdnList.add(memberDTO);
                    groupPersistDTO.setGroupSupervisor(mdnList);
                }*/
                groupPersistDTO.setGroupMemberCount(memCount);
                groupPersistDTO.setETag(rs.getInt(4));
                groupPersistDTO.setGroupType(mappGroupTypeToApp(rs.getInt(5)));
                groupPersistDTO.setAvatar((Integer) rs.getObject(6));
                if(rs.getInt(7) == 1) groupPersistDTO.setBroadcasterCount(1);
                groupPersistDTO.setGroupCreatedBy(rs.getInt(8));
                if(rs.getInt(9) == 1){
                    groupPersistDTO.setLargeGroup(Boolean.TRUE);
                } else if(rs.getInt(9) == 2){
                    groupPersistDTO.setMcxGrpInd(MCX_GRP_INDICATOR);
                    //overriding group member count to 0
                    groupPersistDTO.setGroupMemberCount(MCX_GRP_COUNT);
                }
                groupPersistDTO.setOSMListId(String.valueOf(rs.getInt(10)));
                groupPersistDTO.setGroupProfileId(rs.getString(11));
                groupPersistDTO.setGrpShared((Integer)rs.getObject("GROUP_SHARED"));
                groupPersistDTO.setCorpId(rs.getInt("CORPID"));
                if (null != rs.getObject("VIDEO_PERMISSION")) {
                    groupPersistDTO.setVideoPermission(rs.getInt("VIDEO_PERMISSION"));
                } else {
                    groupPersistDTO.setVideoPermission(KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                }
                groupList.add(groupPersistDTO);
            }

            knLogger.debug(methodName, "subsMdn - ", KnGDPRTemplate.mdn(subsMdn), ", maxMemPerGroup - ", maxMemPerGroup, "GroupList size - ", groupList.size());
            return groupList;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving GroupList - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve GroupList " + e,
                    pttServerId, KnDAOSourceTypes.GRPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pstmt);
        }
    }

    public Collection<KnCorpGroupInfoPersistDTO> selectSubsGroupListPaginated(String subsMdn, int maxMemPerGroup,
                                                                     int fetchSize, int nextToken,
                                                                     boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "selectSubsGroupList(String, int, int, int, boolean, KnPersisterTxn)";
        knLogger.info(methodName, "Entry : subsMdn - ", KnGDPRTemplate.mdn(subsMdn), ", maxMemPerGroup - ", maxMemPerGroup,
                "fetchSize - ", fetchSize, "nextToken - ", nextToken);
        String query = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            //Connection conn = persisterTxn.getDBConnection(pttServerId, false);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, readOnly);
            query = queryMapper.getQuery(GET_SUBS_GROUP_LIST_PAGINATED);
            pstmt = conn.prepareStatement(query);
            int startIndex = getStartIndex(fetchSize, nextToken);
            int endIndex = getEndIndex(fetchSize, nextToken);

            pstmt.setInt(1, startIndex);
            pstmt.setInt(2, endIndex);
            pstmt.setString(3, subsMdn);
            rs = pstmt.executeQuery();
            Collection<KnCorpGroupInfoPersistDTO> groupList = new ArrayList<KnCorpGroupInfoPersistDTO>();
            while (rs.next()) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(rs.getInt(1));
                //multilingual revert change
                if (rs.getString(2) != null) {
                    groupPersistDTO.setGroupDisplayName(new String(rs.getString(2).trim().getBytes("8859_1"), "UTF-8"));
                }
                int memCount = rs.getInt(3);
                if (memCount > maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(GREATER_THAN_LIMIT);
                } else if (memCount == maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(EQUAL_TO_LIMIT);
                } else if (memCount < MIN_MEMBER_LIMIT) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(LESS_THAN_MIN_LIMIT);
                } else if (memCount < maxMemPerGroup) {
                    groupPersistDTO.setMaxGroupMemLimitFlag(LESS_THAN_LIMIT);
                }
                groupPersistDTO.setGroupMemberCount(memCount);
                groupPersistDTO.setETag(rs.getInt(4));
                groupPersistDTO.setGroupType(mappGroupTypeToApp(rs.getInt(5)));
                groupPersistDTO.setAvatar((Integer) rs.getObject(6));
                if(rs.getInt(7) == 1) groupPersistDTO.setBroadcasterCount(1);
                groupPersistDTO.setGroupCreatedBy(rs.getInt(8));
                if(rs.getInt(9) == 1){
                    groupPersistDTO.setLargeGroup(Boolean.TRUE);
                } else if(rs.getInt(9) == 2){
                    groupPersistDTO.setMcxGrpInd(MCX_GRP_INDICATOR);
                    //overriding group member count to 0
                    groupPersistDTO.setGroupMemberCount(MCX_GRP_COUNT);
                }
                groupPersistDTO.setOSMListId(String.valueOf(rs.getInt(10)));
                groupPersistDTO.setGroupProfileId(rs.getString(11));
                groupPersistDTO.setGrpShared((Integer)rs.getObject("GROUP_SHARED"));
                groupPersistDTO.setCorpId(rs.getInt("CORPID"));
                if (null != rs.getObject("VIDEO_PERMISSION")) {
                    groupPersistDTO.setVideoPermission(rs.getInt("VIDEO_PERMISSION"));
                } else {
                    groupPersistDTO.setVideoPermission(KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                }
                groupList.add(groupPersistDTO);
            }
            knLogger.info(methodName, "subsMdn - ", KnGDPRTemplate.mdn(subsMdn), ", maxMemPerGroup - ", maxMemPerGroup,
                    "fetchSize - ", fetchSize, "nextToken - ", nextToken, "GroupList size - ", groupList.size());
            return groupList;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving GroupList - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve GroupList " + e,
                    pttServerId, KnDAOSourceTypes.GRPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pstmt);
        }
    }


    @Override
    public void deleteMcpttPermConfig(String mdn,String corpId) throws KnDAOException {
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        knUserProfileDAO.deleteMcpttPermConfig(mdn,corpId);
    }
    @Override
    public void deleteMcpttPermConfig(List<String> mdnList,String corpId) throws KnDAOException {
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        knUserProfileDAO.deleteMcpttPermConfig(mdnList,corpId);
    }

    @Override
    public Map<String,Integer> getSublistMemberCorpDetails(int sublistId, String corpId,KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSublistMemberCorpDetails(String, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        ResultSet rs = null;
        PreparedStatement pStmt = null;
        Map<String,Integer> memberCoprInfo=new HashMap<>();
        String query = "SELECT MEMBERMDN, MEMBERCORPID FROM DG.CORPLISTMEMBER WHERE CORPLISTID= ?;";
        try {

            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, sublistId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                memberCoprInfo.put(rs.getString("MEMBERMDN").trim(),rs.getInt("MEMBERCORPID"));
            }
        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving GroupList - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve GroupList " + e,
                    pttServerId, KnDAOSourceTypes.GRPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
        }

        return memberCoprInfo;
    }

    @Override
    public Map<String,String> getMdnMcpttIdMap(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.getMdnMcpttIdMap(mdnList, persisterTxn);
    }

    @Override
    public void insertUserProfileHiearchyMap(String userProfileId, List<String> ownerFanIds,String idType,KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMUserProfilHiearchyMapDAO profilHiearchyDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfilHiearchyMapDAO(pttServerId);
        profilHiearchyDao.insertUserProfileHiearchyMap(userProfileId, ownerFanIds,idType, persisterTxn);
    }

    @Override
    public void deleteUserProfileHiearchyMap(String userProfileId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMUserProfilHiearchyMapDAO profilHiearchyDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfilHiearchyMapDAO(pttServerId);
        profilHiearchyDao.deleteUserProfileHiearchyMap(userProfileId,persisterTxn);
    }
    @Override
    public List<String> userProfileIdsHiearchyMap(KnPersisterTxn persisterTxn, List<String> iD_VALUE, boolean readOnly) throws KnDAOException {
        KnXDMUserProfilHiearchyMapDAO profilHiearchyDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfilHiearchyMapDAO(pttServerId);
        return profilHiearchyDao.userProfileIdsHiearchyMap(persisterTxn, iD_VALUE, readOnly);
    }

    @Override
    public List<String> userProfileIdsHiearchyMapFetchSize(KnPersisterTxn persisterTxn, String startIndex, String fetchSize, List<String> iD_VALUE) throws KnDAOException {
        KnXDMUserProfilHiearchyMapDAO profilHiearchyDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfilHiearchyMapDAO(pttServerId);
        return profilHiearchyDao.userProfileIdsHiearchyMapFetchSize(persisterTxn, startIndex, fetchSize, iD_VALUE);
    }

    @Override
    public List<String> getOwnerIDByUserProfileId(String userProfileId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMUserProfilHiearchyMapDAO profilHiearchyDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfilHiearchyMapDAO(pttServerId);
        return profilHiearchyDao.getOwnerIDByUserProfileId(userProfileId, readOnly, persisterTxn);
    }

    public Map<String,List<String>> getUserProfileOwnerList(Collection<String> userProfileIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMUserProfilHiearchyMapDAO profilHiearchyDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfilHiearchyMapDAO(pttServerId);
        return profilHiearchyDao.getUserProfileOwnerList(userProfileIds, readOnly, persisterTxn);
    }

    @Override
    public void removeUserProfileHiearchyMapByOwnerIds(String userProfileId, List<String> ownerFanIds,String idType,KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMUserProfilHiearchyMapDAO profilHiearchyDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfilHiearchyMapDAO(pttServerId);
        profilHiearchyDao.removeUserProfileHiearchyMapByOwnerIds(userProfileId, ownerFanIds,idType, persisterTxn);
    }

    public Set<String> getMDNListByFanIds(Set<Integer> fanIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getMDNListByFanIds(fanIds, readOnly, persisterTxn);
    }

    @Override
    public void insertGroupHiearchyMap(Integer groupId, List<String> ownerFanIds, String idType, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMGroupHiearchyMapDAO groupHiearchyMapDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMGroupHiearchyMapDAO(pttServerId);
        groupHiearchyMapDAO.insertGroupHiearchyMap(groupId,ownerFanIds,idType,persisterTxn);
    }

    @Override
    public void insertGroupHiearchyMap(List<KnCorpGroupInfoPersistDTO> groupInfoPersistDTOList, List<String> ownerFanIds, String idType, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMGroupHiearchyMapDAO groupHiearchyMapDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMGroupHiearchyMapDAO(pttServerId);
        groupHiearchyMapDAO.insertGroupHiearchyMap(groupInfoPersistDTOList, ownerFanIds, idType, persisterTxn);
    }

    @Override
    public void removeGroupHiearchyMapByOwnerIds(Integer groupId, List<String> ownerFanIds, String idType, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMGroupHiearchyMapDAO groupHiearchyMapDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMGroupHiearchyMapDAO(pttServerId);
        groupHiearchyMapDAO.removeGroupHiearchyMapByOwnerIds(groupId,ownerFanIds,idType,persisterTxn);
    }

    @Override
    public void removeGroupHiearchyMapByGroupId(Integer groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMGroupHiearchyMapDAO groupHiearchyMapDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMGroupHiearchyMapDAO(pttServerId);
        groupHiearchyMapDAO.removeGroupHiearchyMapByGroupId(groupId,persisterTxn);
    }

    @Override
    public Map<Integer, List<String>> getGroupOwnerList(List<Integer> groupIds, String idType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMGroupHiearchyMapDAO groupHiearchyMapDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMGroupHiearchyMapDAO(pttServerId);
        return groupHiearchyMapDAO.getGroupOwnerList(groupIds, idType, readOnly, persisterTxn);
    }

    @Override
    public Map<Integer, List<String>> getGroupSharedIdList(List<Integer> groupIds, String idType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMGroupHiearchyMapDAO groupHiearchyMapDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMGroupHiearchyMapDAO(pttServerId);
        return groupHiearchyMapDAO.getGroupSharedIdList(groupIds, idType, readOnly, persisterTxn);
    }

    @Override
    public void insertSharedGroupHierarchyMap(Integer groupId, List<String> hierarchyIds, List<Integer> corpIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMGroupHiearchyMapDAO groupHiearchyMapDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMGroupHiearchyMapDAO(pttServerId);
        groupHiearchyMapDAO.insertSharedGroupHierarchyMap(groupId, hierarchyIds, corpIds, persisterTxn);
    }

    @Override
    public List<int[]> getSharedGroupHierarchyMappings(Integer groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMGroupHiearchyMapDAO groupHiearchyMapDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMGroupHiearchyMapDAO(pttServerId);
        return groupHiearchyMapDAO.getSharedGroupHierarchyMappings(groupId, persisterTxn);
    }

    @Override
    public int countActiveGroupsByCorpPair(Integer ownedCorpId, Integer sharedCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMGroupHiearchyMapDAO groupHiearchyMapDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMGroupHiearchyMapDAO(pttServerId);
        return groupHiearchyMapDAO.countActiveGroupsByCorpPair(ownedCorpId, sharedCorpId, persisterTxn);
    }

    @Override
    public Map<String, String> getUserProfileIdNameBySublistId(Integer corpId, Integer subListId) throws KnDAOException {
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        return knUserProfileDAO.getUserProfileIdNameBySublistId(corpId, subListId);
    }

    @Override
    public List<Integer> getSharedCorpIdFromUserProfielId(String userProfileId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
       KnUserprofileSharedlistDAO userprofileSharedlistDAO =  KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
      return userprofileSharedlistDAO.getSharedCorpIdFromUserProfielId(userProfileId, readOnly, persisterTxn);
    }

    @Override
    public int getSubListMemberCount(int subListId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListDistInfoDAO listDistInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListDistInfoDAO(pttServerId);
        return listDistInfoDAO.getSubListMemberCount(subListId, persisterTxn);
    }

    @Override
    public Map<Integer, Integer> isValidGroupForIdList(List<Integer> groupIds, List<Integer> idList, String idType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMGroupHiearchyMapDAO groupHiearchyMapDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMGroupHiearchyMapDAO(pttServerId);
        return groupHiearchyMapDAO.isValidGroupForIdList(groupIds, idList, idType, readOnly, persisterTxn);
    }

    @Override
    public Map<String, Integer> getPrivacyOptStatus(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getPrivacyOptStatus(mdnList,persisterTxn);
    }

    public Map<String, KnOPDirChgDTO> updateDistinctSubcribersDirectoryBCG(Collection<String> mdnList,
                                                                        Collection<Integer> groupIdLst,
                                                                        Map<String, KnOPDirChgDTO> etags, KnPersisterTxn persisterTxn,String xdmsHome) throws KnDAOException, KnCorpBOException {
        final String methodName = "updateDistinctSubcribersDirectory(Collection<String>, Collection<Integer>, Map<String, KnOPDirChgDTO> etags, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: mdnList size- ", getSize(mdnList), ", groupIdLst size - ", getSize(groupIdLst), "etags - ", KnGDPRTemplate.mapKeyMdn(etags));
        KnXDMSubscriberInfoDAO subscrInfoDao =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        KnCorpGroupInfoUtil groupInfoUtil = new KnCorpGroupInfoUtil();
        Map<Integer, Collection<String>> currentGroupMember = groupInfoUtil.selectGroupMemberListForGroupIds(groupIdLst, xdmsHome, persisterTxn);
        Collection<String> membersMdn = new ArrayList<>() ;
        for (Map.Entry<Integer, Collection<String>> ent : currentGroupMember.entrySet()) {
            for (String member : ent.getValue()) {
                membersMdn.add(member);
            }
        }
        Map<String, KnOPDirChgDTO> etagMap = updateSubscribersDirectroy(membersMdn, etags, persisterTxn);
        Map<String, KnCorpSubscriberDTO> subscrInfoMap = new HashMap<String, KnCorpSubscriberDTO>();
        if (membersMdn != null) {
            subscrInfoMap = subscrInfoDao.getSubsribersCorporateDetails(membersMdn, persisterTxn);
        }
        for (Map.Entry<String, KnOPDirChgDTO> entry : etags.entrySet()) {
            String mdn = entry.getKey();
            KnOPDirChgDTO directory = etagMap.get(mdn);
            KnOPDirChgDTO dir = entry.getValue();
            if (!isObjectNull(directory)) {
                if (!isObjectNull(dir)) {
                    Collection<KnOPDocChgDTO> intialDoc = dir.getDocChgDTO();
                    if (intialDoc == null) {
                        intialDoc = new ArrayList<KnOPDocChgDTO>();
                    }
                    KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
                    if (subsc != null) {
                        directory.setPocHome(subsc.getPocHome());
                        directory.setPresenceHome(subsc.getPresenceHome());
                        directory.setNotfnCapability(subsc.isNotfnCapabiliy());
                        directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                        directory.setClientType(subsc.getClientType());
                    }
                    directory.setDocChgDTO(intialDoc);
                }
            } else {
                if (!isObjectNull(dir)) {
                    KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
                    if (subsc != null) {
                        dir.setPocHome(subsc.getPocHome());
                        dir.setPresenceHome(subsc.getPresenceHome());
                        dir.setNotfnCapability(subsc.isNotfnCapabiliy());
                        directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                        directory.setClientType(subsc.getClientType());
                    }
                    etagMap.put(mdn, dir);
                }
            }
        }
        knLogger.debug(methodName, "mdnList size- ", getSize(mdnList), ", groupIdLst size - ", getSize(groupIdLst), "etags - ", KnGDPRTemplate.mapKeyMdn(etags), "EXIT Point.", KnGDPRTemplate.mapKeyMdn(etagMap));
        return etagMap;
    }

    @Override
    public void getAndUpdateBulkDirectoryEtag(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMDirectoryDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMDirectoryDAO(pttServerId);
        subscriberTable.getAndUpdateBulkDirectoryEtag(mdnList, persisterTxn);
    }

    @Override
    public void updateBulkSubsTS(Set<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMDirectoryDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMDirectoryDAO(pttServerId);
        subscriberTable.updateBulkSubsTS(mdnList, persisterTxn);
    }
    @Override
    public Map<String,Integer> getUserProfileOwnerinfo(ArrayList<String> profileIds,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO userprofileSharedlistDAO =  KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        return userprofileSharedlistDAO.getUserProfileOwnerinfo(profileIds,readOnly, persisterTxn);
    }


    @Override
    public void insertUserProfileSharedList(List<KnUserprofileSharedlistDTO> userprofileSharedlist,KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO profilSharedListDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        profilSharedListDao.insert(userprofileSharedlist,persisterTxn);
    }

    @Override
    public List<String> getXdmUserProfileIdsBySharedCorpId(String sharedCorpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO userprofileSharedlistDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        return userprofileSharedlistDAO.getXdmUserProfileIdsBySharedCorpId(sharedCorpId, readOnly, persisterTxn);
    }

    @Override
    public List<String> getMdnBySharedUserProfileCorpIds(int ownCorpId,List<Integer> sharedCorpids, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO userprofileSharedlistDAO =  KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        return userprofileSharedlistDAO.getMdnBySharedUserProfileCorpIds(ownCorpId,sharedCorpids, persisterTxn);
    }

    @Override
    public int getSystemPreConfigGroupCont(KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSystemPreConfigGroupCont( KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        ResultSet rs = null;
        PreparedStatement pStmt = null;
        Map<String,Integer> memberCoprInfo=new HashMap<>();
        Integer count = null ;
        String query = null;
        try {
            query = queryMapper.getQuery(GET_PRECONFIG_GROUP_COUNT);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pStmt = conn.prepareStatement(query);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pStmt.executeQuery();
            while (rs.next()) {
               count= (rs.getInt(1));
            }
        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving GroupList - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve GroupList " + e,
                    pttServerId, KnDAOSourceTypes.GRPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
        }
        knLogger.debug(methodName, "count ", count);
        return count;
    }

    public List<String> getOwnCorpRegroupMembers(int corpId, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        Collection<KnCorpSubscriberDTO> subsList;
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.getOwnCorpRegroupMembers(corpId, persisterTxn);
    }

    @Override
    public List<String> getSharedGroupMemberBySharedAndOwnCorpids(int ownedCorpId,List<Integer> sharedCorpids, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupSharedListDAO grpSharedListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupSharedListDAO(pttServerId);
        return grpSharedListDAO.getSharedGroupMemberBySharedAndOwnCorpids(ownedCorpId,sharedCorpids,persisterTxn);
    }

    public Map<String, KnOPDirChgDTO> updateSetSubcribersDirectory(Collection<String> mdnList,
                                                                        Map<String, KnOPDirChgDTO> etags, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateSetSubcribersDirectory()";
        knLogger.debug(methodName, "ENTRY: mdnList - ", KnGDPRTemplate.mdnList(mdnList), "etags - ", KnGDPRTemplate.mapKeyMdn(etags));
        KnXDMSubscriberInfoDAO subscrInfoDao =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);

        Map<String, KnOPDirChgDTO> etagMap = updateSubscribersDirectroy(mdnList, etags, persisterTxn);
        Map<String, KnCorpSubscriberDTO> subscrInfoMap = new HashMap<String, KnCorpSubscriberDTO>();
        if (mdnList != null) {
            subscrInfoMap = subscrInfoDao.getSubsribersCorporateDetails(mdnList, persisterTxn);
        }
        for (Map.Entry<String, KnOPDirChgDTO> entry : etags.entrySet()) {
            String mdn = entry.getKey();
            KnOPDirChgDTO directory = etagMap.get(mdn);
            KnOPDirChgDTO dir = entry.getValue();
            if (!isObjectNull(directory)) {
                if (!isObjectNull(dir)) {
                    Collection<KnOPDocChgDTO> intialDoc = dir.getDocChgDTO();
                    if (intialDoc == null) {
                        intialDoc = new ArrayList<KnOPDocChgDTO>();
                    }
                    KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
                    if (subsc != null) {
                        directory.setPocHome(subsc.getPocHome());
                        directory.setPresenceHome(subsc.getPresenceHome());
                        directory.setNotfnCapability(subsc.isNotfnCapabiliy());
                        directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                        directory.setClientType(subsc.getClientType());
                        //sending Notification to Profile MDN..
                        boolean subsUpmBit = KnGeneralUtil.getFeatureBitValue(subsc.getSubsActiveFS2(), USER_PROFILE_MGMT_BIT);
                        knLogger.debug(methodName,"subsUpmBit :",subsUpmBit);
                        if(subsUpmBit){
                            //owner mdn is  0 - Base Mdn,1 - Profile Mdn
                            directory.setNtfyOnAnyMDN(1);
                        }
                    }
                    directory.setDocChgDTO(intialDoc);
                }
            } else {
                if (!isObjectNull(dir)) {
                    KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
                    if (subsc != null) {
                        dir.setPocHome(subsc.getPocHome());
                        dir.setPresenceHome(subsc.getPresenceHome());
                        dir.setNotfnCapability(subsc.isNotfnCapabiliy());
                        directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                        directory.setClientType(subsc.getClientType());
                    }
                    etagMap.put(mdn, dir);
                }
            }
        }
        knLogger.debug(methodName, "EXIT :", KnGDPRTemplate.mapKeyMdn(etagMap));
        return etagMap;
    }

    public Map<Integer,KnCorpGroupInfoPersistDTO> getAllPreconfigGroupInfo(int corpId, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        return corpGroupInfoDAO.getAllPreconfigGroupInfo(corpId, persisterTxn);
    }

    public List<Integer> getIsPreConfiguredParamList(List<Integer> groupIdList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupInfoDAO corpGrpInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpGroupInfoDAO(pttServerId);
        return corpGrpInfoDAO.getPreConfigParamList(groupIdList, persisterTxn);
    }

    @Override
    public List<KnUserprofileSharedlistDTO> getListOfProfilesBySharedCorpIds(Integer sharedCorpId ,Integer ownerCorpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getListOfProfilesBySharedCorpIds( KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        ResultSet rs = null;
        PreparedStatement pStmt = null;
        Map<String,Integer> memberCoprInfo=new HashMap<>();
        Integer count = null ;
        String query = null;
        List<KnUserprofileSharedlistDTO> userprofileSharedlist = new ArrayList<>();
        try {
            query = queryMapper.getQuery(GET_SHARED_LIST_DETAILS);
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            knLogger.debug(methodName, "Executing query - ", query);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, sharedCorpId);
            pStmt.setInt(2, ownerCorpId);
            knLogger.debug(methodName, "Executing query - ", query);
            rs = pStmt.executeQuery();
            while (rs.next()) {
                KnUserprofileSharedlistDTO profileInfo = new KnUserprofileSharedlistDTO(rs.getString("USERPROFILEID"),rs.getInt("OWNEDCORPID"),rs.getInt("SHAREDCORPID"));
                userprofileSharedlist.add(profileInfo);

            }
        }catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving getListOfProfilesBySharedCorpIds - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve getListOfProfilesBySharedCorpIds " + e,
                    pttServerId, KnDAOSourceTypes.GRPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pStmt);
        }
        knLogger.debug(methodName, "Result :- ", userprofileSharedlist);
        return userprofileSharedlist;
    }

    public void updateSubsOsmAuthorizeInAllGroups(String mdn,String isOSMAuthorize, KnPersisterTxn persisterTxn) throws KnDAOException{
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.updateSubsOsmAuthorizeInAllGroups(mdn,isOSMAuthorize, persisterTxn);
    }



    @Override
    public Collection<KnSubscriberUserProfileDTO> getUserProfileListByProfileIds(List<String> userProfileIds) throws KnDAOException {
        String methodName = "getUserProfileListByProfileIds()";
        knLogger.debug(methodName, "Entry:", userProfileIds);
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        Collection<KnSubscriberUserProfileDTO> userProfileList = knUserProfileDAO.getUserProfileListByProfileIds(userProfileIds);
        knLogger.debug(methodName, "--->result from couchbase- ", userProfileList);
        return userProfileList;
    }

    @Override
    public List<KnUserprofileSharedlistDTO> getUserProfileSharedListByUpmId(String userProfileId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO userprofileSharedlistDAO =  KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        return userprofileSharedlistDAO.getUserProfileSharedListByUpmId(userProfileId, persisterTxn);
    }

    @Override
    public Collection<KnMDNInfoDto> getUserProfileAllSubscriberList(String userProfileId, int startIndex, int fetchSize, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUserProfileAllSubscriberList(String, int, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry: --->userProfileId -", userProfileId);
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        Collection<KnMDNInfoDto> mdnList = pocSubscInfoDAO.getUserProfileAllSubscriberList(userProfileId, startIndex, fetchSize, readOnly, persisterTxn);
        knLogger.debug(methodName, "--->mdnList - ", mdnList);
        return mdnList;
    }

    @Override
    public void deleteCorpInfoFromUserProfileSharedList(String userProfileId,List<String> corpIds,KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO profilSharedListDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        profilSharedListDao.deleteCorpInfoFromUserProfileSharedList(userProfileId,corpIds,persisterTxn);
    }

    @Override
    public List<KnCorpContactDTO> getUserProfileAllSubscriberListDetails(String userProfileId,KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getUserProfileAllSubscriberList(String,String,int,int)";
        knLogger.debug(methodName, "Entry: --->userProfileId -", userProfileId);
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        List<KnCorpContactDTO> mdnList = pocSubscInfoDAO.getUserProfileAllSubscriberListDetails(userProfileId,persisterTxn);
        knLogger.debug(methodName, "--->mdnList - ", mdnList);
        return mdnList;
    }
    public Map<String, Integer> commContctListCntForSubsc(KnIPCorpSublistSubscDistDTO subsRequestDTO,
                                                                  KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "commContctListCntForSubsc(KnIPCorpSublistSubscDistDTO,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        KnXDMCorpListDistInfoDAO corpListDist =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListDistInfoDAO(pttServerId);
        Map<String, Integer> commContctListCntForSubsc = corpListDist.commContctListCntForSubsc(subsRequestDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return commContctListCntForSubsc;
    }

    public int getCorpIdFromCorpListInfo(int sublistId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpSublistCount(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        int count = corpListDAO.getCorpIdFromCorpListInfo(sublistId, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return count;
    }
    public Set<Integer> getCorpIdFromMdnList(List<String> mdnList, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        final String methodName = "getCorpIdFromMdnList(List<String>,KnPersisterTxn) ";
        knLogger.debug(methodName, "ENTRY: ");

        KnXDMSubscriberInfoDAO subscriberInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        Set<Integer> corpIds = subscriberInfoDAO.getCorpIdFromMdnList(mdnList, persisterTxn);
        knLogger.debug(methodName, "EXIT Point.");
        return corpIds;
    }

    public List<String> getCommonContactListForMdns(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpSublistCount(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        List<String> result = corpListDAO.getCommonContactListForMdns(mdn, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return result;
    }

    public List<String> getAllSublistContactMdns(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpSublistCount(int, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        List<String> result = corpListDAO.getAllSublistContactMdns(mdn, persisterTxn);
        knLogger.debug(methodName, "EXIT");
        return result;
    }

    @Override
    public void insertIntoMcpttPermInfoForProfileMdns(Collection<KnMcpttPermissionDTO> mcpttMappingDto,List<String> profileMdnsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.insertIntoMcpttPermInfoForProfileMdns(mcpttMappingDto,profileMdnsList, persisterTxn);
    }

    @Override
    public void updateToMcpttPermInfoForProfileMdns(Collection<KnMcpttPermissionDTO> mcpttMappingDto,List<String> profileMdnsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.updateToMcpttPermInfoForProfileMdns(mcpttMappingDto,profileMdnsList ,persisterTxn);
    }

    @Override
    public void deleteFromMcpttPermInfoForProfileMdns(List<String> authMdns, Collection<String> targetMdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        mcpttInfoDAO.deleteFromMcpttPermInfoForProfileMdns(authMdns,targetMdn ,persisterTxn);
    }

    @Override
    public Map<String, Integer> getCommonContactInfoFromMcpttPerm(String authMdn, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getCommonContactInfoFromMcpttPerm(authMdn, readOnly, persisterTxn);
    }

    @Override
    public List<KnUserProfileAssignedDTO> getUserProfileSubsCount(Collection<String> userProfileIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO userprofileSharedlistDAO =  KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        return userprofileSharedlistDAO.getUserProfileSubsCount(userProfileIds, readOnly, persisterTxn);
    }

    public Map<String, KnOPDirChgDTO> updateRegroupSubscribersDirectory(Collection<String> membersMdn,
                                                                        Map<String, KnOPDirChgDTO> etags,
                                                                        KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "updateRegroupSubscribersDirectory(Collection<String>, Map<String, KnOPDirChgDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point :",KnGDPRTemplate.mdnList(membersMdn));
        KnXDMDirectoryDAO directoryDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMDirectoryDAO(pttServerId);
        Map<String, KnOPDirChgDTO> currdirectoryEtag = directoryDAO.updateEtag(membersMdn, persisterTxn, etags);
        KnXDMSubscriberInfoDAO subscrInfoDao =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        Map<String, KnCorpSubscriberDTO> subscrInfoMap = subscrInfoDao.getSubsribersCorporateDetails(membersMdn, persisterTxn);
        for (String mdn : membersMdn) {
            mdn = mdn.trim();
            KnOPDirChgDTO directory = currdirectoryEtag.get(mdn);
            KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
            if (subsc != null) {
                directory.setPocHome(subsc.getPocHome());
                directory.setPresenceHome(subsc.getPresenceHome());
                directory.setProtoVersion(String.valueOf(subsc.getClientPVmajorVer()));
                directory.setNotfnCapability(subsc.isNotfnCapabiliy());
                directory.setClientType(subsc.getClientType());
            }
        }
        knLogger.debug(methodName, "EXIT Point.",currdirectoryEtag);
        return currdirectoryEtag;
    }

    @Override
    public Map<String,List<String>> getAuAndCommonTuMapping(List<String> authMdns, List<String> targetMdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getAuAndCommonTuMapping(authMdns,targetMdns ,persisterTxn);
    }

    public Map<String, List<KnMcpttPermissionDTO>> getTargetMdnListPermissions(List<String> targetMdns, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getTargetMdnListPermissions(targetMdns ,persisterTxn);
    }

    public List<Integer> getSharedCorpIds(int groupId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getSharedCorpIds(groupId, xdmsHome, readOnly, persisterTxn);
    }

    @Override
    public KnCorpSubsStatsRespDTO getSubscriberStats(KnIPCorpInfoDTO corpInfoDTO, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getSubscriberStats(int corpId, boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnCorpStatsDAO statsInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMStatsDAO(pttServerId);
        return statsInfoDAO.getSubscriberStats(corpInfoDTO, readOnly, persisterTxn);
    }

    @Override
    public KnCorpDeviceStatsRespDTO getDeviceStats(String corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getDeviceStats(String corpId, boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnCorpStatsDAO devStatsDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMStatsDAO(pttServerId);
        return devStatsDAO.getDeviceStats(corpId, readOnly, persisterTxn);
    }

    public String getCorporateFS(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getDeviceStats(String corpId, boolean readOnly, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnXDMCorpInfoDAO corpInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpInfoDAO(pttServerId);
        return corpInfoDAO.selectCorporateFS(corpId, readOnly, persisterTxn);
    }

    public void updateCorpFs(String corpFs,String corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "selectCorporateFS(String corpId, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnXDMCorpInfoDAO corpInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpInfoDAO(pttServerId);
        corpInfoDAO.updateCorpFs(corpFs,corpId, persisterTxn);
    }

    public void modifyBulkGroupProperties(List<KnXDMGroupPropertyInfoDTO> bulkGroupProperties, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "modifyBulkGroupProperties(List<KnXDMGroupPropertyInfoDTO> bulkGroupProperties, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY: ");
        KnXDMCorpGroupInfoDAO groupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        groupInfoDAO.modifyBulkGroupProperties(bulkGroupProperties, persisterTxn);
    }

    @Override
    public List<String> getProfileMdns(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getProfileMdns(mdnList, persisterTxn);
    }

    public List<String> getNonExisitingGroupMemberInPrivSublist(Integer groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListMemberDAO corpListMemberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        return corpListMemberDAO.getNonExisitingGroupMemberInPrivSublist(groupId, persisterTxn);
    }


    @Override
    public int getCorpDeviceCount(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpDeviceDAO deviceInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMDeviceDAO(pttServerId);
        return deviceInfoDAO.getCorpDeviceCount(corpId, readOnly, persisterTxn);
    }

    public KnCorpGroupInfoPersistDTO getGroupBasicDetails(int groupId,  KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        return corpGroupInfoDAO.getGroupBasicDetails(groupId , persisterTxn);
    }

    @Override
    public Collection<KnCorpGroupInfoPersistDTO> getSubsAbdgGroupList(String subsMdn, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        final String methodName = "getSubsAbdgGroupList(String , KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : subsMdn - ", KnGDPRTemplate.mdn(subsMdn));
        String query = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            Connection conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, true);
            query = queryMapper.getQuery(GET_SUBS_ABDG_GROUP_LIST);
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, subsMdn);
            pstmt.setInt(2, GROUP_CREATED_BY_ABDG);
            rs = pstmt.executeQuery();
            knLogger.debug(methodName, "Successfully Executed query - ", query);
            Collection<KnCorpGroupInfoPersistDTO> groupList = new ArrayList<KnCorpGroupInfoPersistDTO>();
            while (rs.next()) {
                KnCorpGroupInfoPersistDTO groupPersistDTO = new KnCorpGroupInfoPersistDTO();
                groupPersistDTO.setGroupId(rs.getInt(1));
                if (rs.getString(2) != null) {
                    groupPersistDTO.setGroupDisplayName(new String(rs.getString(2).trim().getBytes("8859_1"), "UTF-8"));
                }
                groupPersistDTO.setETag(rs.getInt(3));
                groupPersistDTO.setGroupType(mappGroupTypeToApp(rs.getInt(4)));
                groupPersistDTO.setAvatar((Integer) rs.getObject(5));
                groupPersistDTO.setGroupCreatedBy(rs.getInt(6));
                groupPersistDTO.setCorpId(rs.getInt(7));
                if (null != rs.getObject("VIDEO_PERMISSION")) {
                    groupPersistDTO.setVideoPermission(rs.getInt("VIDEO_PERMISSION"));
                } else {
                    groupPersistDTO.setVideoPermission(KnConstants.DEFAULT_VIDEO_PERMISSION_VALUE);
                }
                groupList.add(groupPersistDTO);
            }

            knLogger.info(methodName, "subsMdn - ", KnGDPRTemplate.mdn(subsMdn), "GroupList size - ", groupList.size());
            return groupList;

        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while retrieving abdgGroupList - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve GroupList " + e,
                    pttServerId, KnDAOSourceTypes.GRPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pstmt);
        }
    }

    public void deleteGroupHierarchy(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteGroupHierarchy(int,KnPersisterTxn)";
        KnXDMCorpGroupInfoDAO groupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        groupInfoDAO.deleteGroupHierarchy(groupId, persisterTxn);
    }

    public Map<String, Map<String, Object>> getCorpAllInternalSubscribers(Collection<Integer> idListExistInDB, int idType, int corpId, int
            maxAllowedContactCount, int fetchSize, int nextToken, int sortType, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getCorpAllInternalSubscribers(idListExistInDB, idType, corpId, maxAllowedContactCount, fetchSize, nextToken, sortType, persisterTxn);
    }

    public int getCorpAllInternalSubscribersCount(Collection<Integer> idListExistInDB, int idType, int corpId,
            KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getCorpAllInternalSubscribersCount(idListExistInDB, idType, corpId, persisterTxn);
    }

    public List<String> getFirstNetFanIdsByFanIds(List<String> fanIds, KnPersisterTxn persisterTxn, boolean readOnly) throws KnDAOException {
        KnXdmFanDetailsDao fanDetailsDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXdmFanDetailsDao(pttServerId);
        return fanDetailsDao.getFirstNetFanIdsByFanIds(fanIds, persisterTxn, readOnly);
    }

    @Override
    public Collection<Integer> getFanList(int corpId, Collection<Integer> idListExistInReq, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        KnXdmFanDetailsDao fanDetailsDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXdmFanDetailsDao(pttServerId);
        return fanDetailsDao.getFanList(corpId, idListExistInReq, persisterTxn);
    }

    @Override
    public boolean isCommonContactList(Collection<Integer> sublistIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        return corpListDAO.isCommonContactList(sublistIds, persisterTxn);
    }

    public Map<String,Map<String,Integer>> getMdnsFanBanInfo(Collection<String> mdnList, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        String methodName = "getMdnsFanBanInfo(List<String>, KnPersisterTxn)";
        knLogger.info(methodName, "Entry : Input DTO passed mdnList size is - ", getSize(mdnList));
        Map<String,Map<String,Integer>> mdnsFanBanInfo = new HashMap<>();
        var mdnListArray = new ArrayList<>(mdnList);
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var mdnsFanBanInfoFromDb = pocSubscInfoDAO.getMdnsFanBanInfo(subsList, readOnly, persisterTxn);
            if (mdnsFanBanInfoFromDb != null && !mdnsFanBanInfoFromDb.isEmpty()){
                mdnsFanBanInfo.putAll(mdnsFanBanInfoFromDb);
            }
        }
        knLogger.debug(methodName, "List size from DB :  ", mdnsFanBanInfo.size());
        return mdnsFanBanInfo;
    }

    @Override
    public Map<Integer, Integer> getIdValueGroupIdMap(List<Integer> groupIds, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnGroupHierarchyDAO groupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createKnGroupHierrarchyDAO(pttServerId);
        return groupInfoDAO.getIdValueGroupIdMap(groupIds, readOnly, persisterTxn);
    }

    public List<Integer> getDistinctFanInfo(Collection<String> mdnList, KnPersisterTxn persisterTxn, boolean readOnly) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        String methodName = "getDistinctFanInfo(Collection<String>, KnPersisterTxn, Boolean)";
        knLogger.info(methodName, "Entry : Input DTO passed mdnList size is - ", getSize(mdnList));
        List<Integer> distFanInfo = new ArrayList<>();
        var mdnListArray = new ArrayList<>(mdnList);
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var mdnsFanBanInfoFromDb = pocSubscInfoDAO.getDistinctFanInfo(subsList, persisterTxn, readOnly);
            if (mdnsFanBanInfoFromDb != null && !mdnsFanBanInfoFromDb.isEmpty()) {
                distFanInfo.addAll(mdnsFanBanInfoFromDb);
            }
        }
        knLogger.debug(methodName, "List size from DB :  ", distFanInfo.size());
        return distFanInfo;
    }

    @Override
    public Map<String, Integer> getOwnerAndSharedContextIdByGroupIds(List<Integer> groupIds, String idType, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMGroupHiearchyMapDAO groupHiearchyMapDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMGroupHiearchyMapDAO(pttServerId);
        return groupHiearchyMapDAO.getOwnerAndSharedContextIdByGroupIds(groupIds, idType, readOnly, persisterTxn);
    }

    public List<Integer> getDistinctBanInfo(Collection<String> mdnList, KnPersisterTxn persisterTxn, boolean readOnly) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMSubscriberInfoDAO(pttServerId);
        String methodName = "getDistinctBanInfo(Collection<String>, KnPersisterTxn, Boolean)";
        knLogger.info(methodName, "Entry : Input DTO passed mdnList size is - ", getSize(mdnList));
        List<Integer> distBanInfo = new ArrayList<>();
        var mdnListArray = new ArrayList<>(mdnList);
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        List<Integer> mdnsFanBanInfoFromDb;
        for (var subsList : subsLists) {
            mdnsFanBanInfoFromDb = pocSubscInfoDAO.getDistinctBanInfo(subsList, persisterTxn, readOnly);
            if (mdnsFanBanInfoFromDb != null && !mdnsFanBanInfoFromDb.isEmpty()) {
                distBanInfo.addAll(mdnsFanBanInfoFromDb);
            }
        }
        knLogger.debug(methodName, "List size from DB :  ", distBanInfo.size());
        return distBanInfo;
    }

    @Override
    public Map<Integer, Integer> getCorpIdAndLargeGroupFlagMap(Collection<Integer> corpIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpInfoDAO corpInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpInfoDAO(pttServerId);
        return corpInfoDAO.getCorpIdAndLargeGroupFlagMap(corpIds, persisterTxn);
    }

    public void updateAllGroupsCorpGroupMemberCountEntry(Collection<Integer> groupIdsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberCountDAO corpGroupMemberCount = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberCountDAO(pttServerId);
        corpGroupMemberCount.updateAllGroupsCorpGroupMemberCountEntry(groupIdsList, persisterTxn);
    }

    @Override
    public List<KnIdDetailsDTO> getBanDetailsByBanFanId(List<Integer> idList, int idType, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMBanDetailsDAO banDetailsDao = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXdmBanDetailsDao(pttServerId);
        return banDetailsDao.getBanDetailsByBanFanId(idList, idType, persisterTxn);
    }

    @Override
    public Map<Integer, Integer> getGroupHierarchyMap(List<Integer> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnGroupHierarchyDAO groupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createKnGroupHierrarchyDAO(pttServerId);
        return groupInfoDAO.getGroupHierarchyMap(groupIds, persisterTxn);
    }

    @Override
    public Set<String> getProfileMdnList(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO userprofileSharedlistDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        return userprofileSharedlistDAO.getProfileMdnList(groupId, persisterTxn);
    }

    @Override
    public int getGroupMemCountAndList(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO userprofileSharedlistDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        return userprofileSharedlistDAO.getGroupMemCountAndList(groupId, persisterTxn);
    }

    @Override
    public Map<Integer, Integer> getGroupMemberCount(Set<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO userprofileSharedlistDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        return userprofileSharedlistDAO.getGroupMemCount(groupIds, persisterTxn);
    }

    @Override
    public Map<Integer, Integer> getGroupAllMemCount(Set<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO userprofileSharedlistDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        return userprofileSharedlistDAO.getGroupAllMemCount(groupIds, persisterTxn);
    }

    @Override
    public Map<String, Integer> getSubsDetails(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO userprofileSharedlistDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        return userprofileSharedlistDAO.getSubsDetails(mdnList, persisterTxn);
    }

    @Override
    public int getUserPfofileIdCount(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO userprofileSharedlistDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        return userprofileSharedlistDAO.getUserPfofileIdCount(groupId, persisterTxn);
    }

    @Override
    public Map<Integer, Integer> getProfileIdCount(Set<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO userprofileSharedlistDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        return userprofileSharedlistDAO.getProfileIdCount(groupIds, persisterTxn);
    }

    @Override
    public int getMemCountBasedOnLocWatcher(int groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO userprofileSharedlistDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        return userprofileSharedlistDAO.getMemCountBasedOnLocWatcher(groupId, persisterTxn);
    }

    @Override
    public Set<Integer> getGroupIdBasedOnMdn(String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO userprofileSharedlistDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        return userprofileSharedlistDAO.getGroupIdBasedOnMdn(mdn, persisterTxn);
    }

    @Override
    public int getMdnCountBasedOnUPMID(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO userprofileSharedlistDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        return userprofileSharedlistDAO.getMdnCountBasedOnUPMID(groupIds, persisterTxn);
    }

    @Override
    public Set<String> getProfileMdns(Set<Integer> groupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnUserprofileSharedlistDAO userprofileSharedlistDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMUserProfileSharedListDAO(pttServerId);
        return userprofileSharedlistDAO.getProfileMdns(groupId, persisterTxn);
    }
    @Override
    public List<KnCorpGroupInfoDTO> getGroupsDetailsWithoutMembers(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getGroupsDetailsWithoutMembers(Collection<Integer>,KnPersisterTxn)";
        KnXDMCorpGroupInfoDAO groupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getGroupsDetailsWithoutMembers(groupIds, persisterTxn);
    }

    public long getCorporateEtagOnCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpInfoDAO corpInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpInfoDAO(pttServerId);
        return corpInfoDAO.getCorporateEtagOnCorpId(corpId, persisterTxn);
    }

    public String updateCatAccessPermSet(String extCorpId, String catAccessPermSet, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpInfoDAO corpInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpInfoDAO(pttServerId);
        return corpInfoDAO.updateCatAccessPermSet(extCorpId, catAccessPermSet, persisterTxn);
    }

    @Override
    public void updateBulkGroupMemberListSupervisorList(Map<Integer, Collection<KnCorpGroupMemberDTO>> supervisorMemberListMap,
                                                        KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateBulkGroupMemberListSupervisorList(Map<Integer, List<KnCorpGroupMemberDTO>>, String)";
        KnXDMCorpGroupMemberListDAO goupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        goupMemberListDAO.updateBulkGroupMemberListSupervisorList(supervisorMemberListMap, persisterTxn);
    }
    public void deleteBulkSubscPrivateContactList(Map<Integer, List<String>> groupIdVsRemovedMdnsListMap, Map<Integer, Integer>
            groupIdVsGroupPrivateListMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "deleteSubscPrivateContactList(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpListMemberDAO corpListMemDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        corpListMemDAO.deleteBulkSubscPrivateContactList(groupIdVsRemovedMdnsListMap, groupIdVsGroupPrivateListMap, persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    /**
     * This method returns the distinct profileId and profile details for MDN in extSubslist.
     *
     * @param extSubsList
     * @param persisterTxn
     * @return Map<Integer, KnExtProfileDetails>
     * @throws KnDAOException
     */
    public Map<String, Integer> getExtSubsrProfilelistMap(List<String> extSubsList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnExtSubscrInfoDAO extSubscDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createExtSubscrInfoDAO(pttServerId);
        return extSubscDAO.getExtSubsrProfilelistMap(extSubsList, persisterTxn);
    }
    @Override
    public Map<String, Integer> getGroupCountByNameForGroupsName(Collection<String> groupNames, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupCountByName(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry : groupName - ", groupNames, ", corpId - ", corpId);
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String query = null;
        Map<String, Integer> returnData = new HashMap<>();
        int index = 1;
        try {
            KnQueryMapper queryMapper = KnQueryMapper.getInstance();
            query = queryMapper.getQuery(GET_GROUP_COUNT_BY_NAME_FOR_GROUPS);
            //conn = persisterTxn.getDBConnection(pttServerId, false);
            conn = persisterTxn.getDBConnection(pttServerId, KnDBConst.DataStores.XDM_SHARED_DATA, false);
            query = com.kodiak.common.dao.KnDbUtil.formCommaSeperatedQuesMarks(groupNames,query,"GROUPNAMES");
            pstmt = conn.prepareStatement(query);
            for(String groupName : groupNames){
                if (groupName != null) {
                    //multilingual revert changes
                    groupName = new String(groupName.getBytes("UTF-8"), "8859_1");
                }
                pstmt.setString(index++,groupName);
            }
            pstmt.setInt(index,corpId);

            rs = pstmt.executeQuery();
            if (rs.next()) {

                returnData.put(rs.getString(1), rs.getInt(2));
            }
            return returnData;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while getGroupCountByName - ", e);
            throw KnDbUtil.processException(e, "Failed to retrieve group member list " + e,
                    pttServerId, KnDAOSourceTypes.GRPINFO, query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closePrepareStmt(pstmt);
            knLogger.debug(methodName, "groupName - ", groupNames, ", corpId - ", corpId, "EXIT : No of groups with the given name -", returnData);
        }
    }

    @Override
    public Set<Integer> getCorpGroupByOSMListIdMap(Map<Integer, Integer> groupOSMListIdMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpGroupByOSMListIdMap(Map<Integer, Integer> , KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        //retrieve subsc info
        return groupInfoDAO.getCorpGroupByOSMListIdMap(groupOSMListIdMap, persisterTxn);

    }

    @Override
    public void modifyBulkGroupName(Map<Integer, String> grpIdvsDisplayName, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyBulkGroupName(Map<Integer, String> , KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        groupInfoDAO.modifyBulkGroupName(grpIdvsDisplayName, persisterTxn);
    }

    @Override
    public void modifyBulkGroupAvatar(Map<Integer, Integer> tempGrpIdvsAvatar, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyBulkGroupAvatar(Map<Integer, Integer> , KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        groupInfoDAO.modifyBulkGroupAvatar(tempGrpIdvsAvatar, persisterTxn);
    }

    @Override
    public void modifyBulkGroupOSMListId(Map<Integer, KnIPCorpGroupInfoDTO> grpIdvsOSMListIdMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyBulkGroupOSMListId(Map<Integer, KnIPCorpGroupInfoDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        groupInfoDAO.modifyBulkGroupOSMListId(grpIdvsOSMListIdMap, persisterTxn);
    }

    @Override
    public Map<Integer, Collection<Integer>> getBulkGroupsSublistListFromDB(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupListRefDAO corpGroupListRefDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupListRefDAO(pttServerId);
        return corpGroupListRefDAO.getBulkGroupsSublistListFromDB(groupIds, persisterTxn);
    }

    @Override
    public void updateBulkGroupType(Map<Integer, Integer> grpIdvsIsGroupTypeChanged, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateBulkGroupType(Map<Integer, Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        groupInfoDAO.updateBulkGroupType(grpIdvsIsGroupTypeChanged, persisterTxn);
    }

    @Override
    public Map<Integer, Map<String, KnCorpGroupMemberDTO>> getBulkGroupMembersList(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getBulkGroupMembersList(List<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupMemberListDAO grpMemListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return grpMemListDAO.getBulkGroupMembersList(groupIds, persisterTxn);
    }

    @Override
    public Map<Integer, KnCorpGroupMemberDTO> getBulkGroupMembersListMap(List<Integer> groupIds, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getBulkGroupMembersList(List<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupMemberListDAO grpMemListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return grpMemListDAO.getBulkGroupMembersListMap(groupIds, mdn, persisterTxn);
    }

    @Override
    public void modifyBulkGroupOverrdeDND(Map<Integer, Integer> groupIdVsOverrideDnd, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        groupInfoDAO.modifyBulkGroupOverrdeDND(groupIdVsOverrideDnd, persisterTxn);
    }


    @Override
    public void modifyBulkGroupLmrInteropCapable(Map<Integer, Integer> grpIdvsIsLmrInteropFeatureChangedMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyBulkGroupLmrInteropCapable(Map<Integer, Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        groupInfoDAO.modifyBulkGroupLmrInteropCapable(grpIdvsIsLmrInteropFeatureChangedMap, persisterTxn);
    }

    @Override
    public void modifyBulkGroupUGWParameter(Map<Integer, Integer> groupIdVsUgwParameterMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyBulkGroupUGWParameter(Map<Integer, Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        groupInfoDAO.modifyBulkGroupUGWParameter(groupIdVsUgwParameterMap, persisterTxn);
    }

    @Override
    public void modifyBulkGroupRecordingFsParameter(Map<Integer, Integer> groupIdVsRecordingFsParameterMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyBulkGroupRecordingFsParameter(Map<Integer, Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        groupInfoDAO.modifyBulkGroupRecordingFsParameter(groupIdVsRecordingFsParameterMap, persisterTxn);
    }

    @Override
    public void modifyBulkGroupEmergAttributes(Map<Integer, KnIPCorpGroupInfoDTO> groupInfoDTOMap, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyBulkGroupEmergAttributes(Map<Integer, KnIPCorpGroupInfoDTO>, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupInfoDAO(pttServerId);
        groupInfoDAO.modifyBulkGroupEmergAttributes(groupInfoDTOMap, persisterTxn);
    }

    @Override
    public Map<Integer, KnCorpGroupInfoPersistDTO> selectBulkGroupBasicInfo(Map<Integer, Integer> groupCorpMap, int clientIntf, KnPersisterTxn persisterTxn, Boolean hiearchyCall) throws KnDAOException {
        String methodName = "selectBulkGroupBasicInfo(Map<Integer, Integer> , KnPersisterTxn)";
        knLogger.debug(methodName, "Entry :");
        KnCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.selectBulkGroupBasicInfo(groupCorpMap,clientIntf, persisterTxn,hiearchyCall);
    }

    @Override
    public Map<Integer, KnCorpGroupInfoPersistDTO> getBulkGroupBasicInfoDetailsWithoutCorpId(List<Integer> groupId, int clientIntf, KnPersisterTxn persisterTxn, Boolean hiearchyCall) throws KnDAOException {
        String methodName = "getBulkGroupBasicInfoDetailsWithoutCorpId(List<Integer> , int, KnPersisterTxn, Boolean)";
        knLogger.debug(methodName, "Entry :");
        KnCorpGroupInfoDAO groupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getBulkGroupBasicInfoDetailsWithoutCorpId(groupId,clientIntf, persisterTxn,hiearchyCall);
    }
    @Override
    public Map<Integer, List<String>> getNonExisitingGroupMemberInPrivSublistForGroups(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListMemberDAO corpListMemberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpListMemberDAO(pttServerId);
        return corpListMemberDAO.getNonExisitingGroupMemberInPrivSublistForGroups(groupIds, persisterTxn);
    }

    public Map<String, KnOPDirChgDTO> updateSubcribersEtagTables(Collection<String> mdnList, KnPersisterTxn
            persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException {
        final String methodName = "updateSubcribersImpactedTables(Collection<String>, KnPersisterTxn,  Map<String, KnOPDirChgDTO> )";
        knLogger.debug(methodName, "ENTRY :");
        KnXDMCorpResourceListIndexDocDAO resourceDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpResourceListIndexDocDAO(pttServerId);
        Map<String, KnOPDocChgDTO> currResourcEtag = resourceDAO.updateEtag(mdnList, persisterTxn);
        knLogger.debug(methodName, "currResourcEtagcurrResourc :", KnGDPRTemplate.mapKeyMdn(currResourcEtag));
        KnXDMDirectoryDAO directoryDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMDirectoryDAO(pttServerId);
        Map<String, KnOPDirChgDTO> currdirectoryEtag = directoryDAO.updateEtag(mdnList, persisterTxn, etagMap);
        knLogger.debug(methodName, "currdirectoryEtagcurrdirectoryEtag :", KnGDPRTemplate.mapKeyMdn(currdirectoryEtag));
        KnXDMSubscriberInfoDAO subscriberDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        Map<String, KnCorpSubscriberDTO> subscrInfoMap = subscriberDAO.getSubsribersCorporateDetails(mdnList, persisterTxn);

        /*for (String mdn : currdirectoryEtag.keySet()) {
            Collection<KnOPDocChgDTO> docList = new ArrayList<KnOPDocChgDTO>();
            mdn = mdn.trim();
            if(currResourcEtag.get(mdn) != null) {
                docList.add(currResourcEtag.get(mdn));
                KnOPDirChgDTO directory = currdirectoryEtag.get(mdn);
                directory.setDocChgDTO(docList);
                KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
                if (subsc != null) {
                    directory.setPocHome(subsc.getPocHome());
                    directory.setPresenceHome(subsc.getPresenceHome());
                    directory.setNotfnCapability(subsc.isNotfnCapabiliy());
                    directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                    directory.setClientType(subsc.getClientType());
                    if(null != subsc.getUserProfileIndex()) {
                        directory.setNtfyOnAnyMDN(subsc.getUserProfileIndex() > 0 ? 1 : 0);
                    }
                }
            }
        }
        List<String> tempList = new ArrayList<>(mdnList);
        tempList.removeAll(currdirectoryEtag.keySet());
        knLogger.debug(methodName, "EXIT:Mismatched mdnList for whom DocDiff notification could not be sent", KnGDPRTemplate.mdnList(tempList));*/
        knLogger.debug(methodName, "currdirectoryEtagcurrdire :",  KnGDPRTemplate.mapKeyMdn(currdirectoryEtag));
        return currdirectoryEtag;
    }

    @Override
    public Map<Integer, Integer> getMaxLocWatchersCount(Collection<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getMaxLocWatchersCount()";
        KnXDMCorpGroupInfoDAO groupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getMaxLocWatchersCount(groupIds, persisterTxn);
    }

    public Map<Integer, Integer> getGroupMemsCounts(Collection<Integer> groupIds , KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberCountDAO groupMemberCountDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupMemberCountDAO(pttServerId);
        return groupMemberCountDAO.getGroupMemsCounts(groupIds , persisterTxn);
    }

    @Override
    public List<KnCorpUserProfileDTO> getAllDistinctUPMByCorp(String corpId) throws KnDAOException {
        KnUserProfileDAO knUserProfileDAO = new KnUserProfileDAO(pttServerId);
        return knUserProfileDAO.getAllDistinctUPMByCorp(corpId);
    }

    public Map<Integer, Integer> getCorpIdfromGroupID(List<String> groupIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getCorpIdfromGroupID(int,KnPersisterTxn)";
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        return corpGroupInfoDAO.getCorpIdfromGroupID(groupIds, xdmsHome, persisterTxn);
    }


    @Override
    public Map<String, Integer> getSubscriberBroadcastGroupCount(List<String> mdns, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMCorpGroupMemberListDAO(pttServerId);
        return groupMemberListDAO.getSubscriberBroadcastGroupCount(mdns,persisterTxn);
    }

    public Set<String> getGroupMemsList(Collection<Integer> groupIds , KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupMemberListDAO groupMemberListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                .createXDMCorpGroupMemberListDAO(pttServerId);
        return groupMemberListDAO.getGroupMemsList(groupIds , persisterTxn);
    }

    @Override
    public boolean checkValidHierarchySubs(List<String> mdnList,Map<String, Object> customParams, KnPersisterTxn persisterTxn)
            throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberTable.checkValidHierarchySubs(mdnList, customParams, persisterTxn);
    }

    @Override
    public KnCorpGroupDTO ifGroupExistsForCorpId(String groupId, String corpId) {
        String methodName = "ifGroupExistsForCorpId(String,String)";
        knLogger.debug(methodName, "Entry:", groupId, corpId);
        Connection conn = null;
        String queryForGroupId = "SELECT CORPGROUPID,CORPID,GROUP_SHARED FROM DG.CORPGROUPINFO WHERE CORPGROUPID = ?";
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        KnCorpGroupDTO groupDTO = new KnCorpGroupDTO();
        try {
            conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
            pStmt = conn.prepareStatement(queryForGroupId);
            pStmt.setInt(1, Integer.parseInt(groupId));
            knLogger.info(methodName, "Executing query - ", "'", queryForGroupId, "'");
            rs = pStmt.executeQuery();
            if (rs.next()) {
                groupDTO.setGroupId(rs.getInt(1));
                groupDTO.setCorpId(rs.getInt(2));
                groupDTO.setGrpShared(rs.getInt(3));
            }
        } catch (Exception e) {
            knLogger.error("Exception::", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        knLogger.info(methodName, "Exit: ");
        return groupDTO;
    }
    @Override
    public boolean sharedCorpCheck(String groupId, String sharedCorpId, String ownerCorpId) {
        String methodName = "sharedCorpCheck(String,String,String)";
        knLogger.debug(methodName, "Entry::", groupId, sharedCorpId, ownerCorpId);
        Connection conn = null;
        String queryForGroupId = "SELECT CORPGROUPID ,SHAREDCORPID,OWNEDCORPID FROM DG.CORPGRP_SHAREDLIST WHERE OWNEDCORPID = ? AND SHAREDCORPID = ? AND CORPGROUPID = ?";
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        boolean result = false;
        try {
            conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
            pStmt = conn.prepareStatement(queryForGroupId);
            pStmt.setInt(1, Integer.parseInt(ownerCorpId));
            pStmt.setInt(2, Integer.parseInt(sharedCorpId));
            pStmt.setInt(3, Integer.parseInt(groupId));
            knLogger.debug(methodName, "Executing query - ", "'", queryForGroupId, "'");
            rs = pStmt.executeQuery();
            result = rs.next();
        } catch (Exception e) {
            knLogger.error("Exception::", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        knLogger.info(methodName, "EXIT: ", "result-->", result);
        return result;
    }
    @Override
    public int isGroupValidRequest(int groupId, Collection<Integer> idContextIdList, int idType, List<String> existingMemLists, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "CUSTOM : isGroupValid(corpId, Collection<Integer>, int, KnPersisterTxn) ";
        knLogger.debug(methodName, "ENTRY : ", groupId, idContextIdList, idType);
        Statement stmt = null;
        ResultSet rs = null;
        int memCount = 0;
        String query = null;
        Connection conn = null;
        try {
            if (idType == 1) {
                knLogger.debug(methodName, "idType == 1");
                query = "SELECT COUNT(1) FROM DG.SUBSCRIBER_ADDLINFO WHERE MDN in (MEMBERMDNLIST) AND BAN_ID IN (IDLIST)";
            } else if (idType == 2) {
                knLogger.debug(methodName, "idType == 2");
                query = "SELECT COUNT(1) FROM DG.SUBSCRIBER_ADDLINFO WHERE MDN in (MEMBERMDNLIST) AND FAN_ID IN (IDLIST)";
            }
            query = replaceContactWithValue(query, "MEMBERMDNLIST", formIntegerCommaSeperatedIdListString(existingMemLists));
            query = replaceContactWithValue(query, "IDLIST", formIntegerCommaSeperatedIdList(new ArrayList<>(idContextIdList)));
            conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            knLogger.debug(methodName, "Execution successfull - ", query);
            while (rs.next()) {
                memCount = rs.getInt(1);
            }
            knLogger.info(methodName, "EXIT: ", "memCount-->", memCount);
            return memCount;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected Exception occured while isGroupValid - ",
                    new KnException(com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.DAO.INTERNAL_ERROR, e.getMessage(), e));
            throw KnDbUtil.processException(e, "Failed while isGroupValid -" + e,
                    pttServerId, "DG.SUBSCRIBER_ADDLINFO", query);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(stmt);
            KnDbUtil.closeConnection(conn);
        }
    }
    @Override
    public Map<Integer, List<KnCorpGroupDTO>> sharedGroupIdContextIdMap(int groupId, Map<Integer, List<Integer>> idIncontextIdMap, String xdmsHome, KnPersisterTxn persisterTxn) {
        String methodName = "sharedGroupIdContextIdMap(int,List<Integer>,String,KnPersisterTxn)";
        knLogger.info(methodName, "Entry::", groupId, idIncontextIdMap);
        Connection conn = null;
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        Map<Integer, List<KnCorpGroupDTO>> groupIdContextIdMap = new HashMap<>();
        List<KnCorpGroupDTO> groupDTOList = new ArrayList<>();
        String placeholders1 = "";
        String placeholders2 = "";
        if (idIncontextIdMap.get(1) != null) {
            placeholders1 = String.join(",", Collections.nCopies(idIncontextIdMap.get(1).size(), "?"));
        }
        if (idIncontextIdMap.get(2) != null) {
            placeholders2 = String.join(",", Collections.nCopies(idIncontextIdMap.get(2).size(), "?"));
        }
        String query = "SELECT CORPGROUPID, ID_VALUE, ID_TYPE FROM DG.GROUP_HIERARCHY_MAP WHERE CORPGROUPID = ? AND ((ID_VALUE IN (" + placeholders1 + ") AND ID_TYPE = 1) OR (ID_VALUE IN (" + placeholders2 + ") AND ID_TYPE IN (2,3)))";
        try {
            conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(xdmsHome), true);
            pStmt = conn.prepareStatement(query);
            pStmt.setInt(1, groupId);
            int index = 2;
            for (int type : Arrays.asList(1, 2)) {
                List<Integer> ids = idIncontextIdMap.get(type);
                if (ids != null) {
                    for (Integer id : ids) {
                        pStmt.setInt(index++, id);
                    }
                }
            }
            knLogger.info(methodName, "Executing query - ", "'", query, "'");
            rs = pStmt.executeQuery();
            while (rs.next()) {
                KnCorpGroupDTO groupDTO = new KnCorpGroupDTO();
                groupDTO.setGroupId(rs.getInt(1));
                groupDTO.setIdValue(rs.getInt(2));
                int idType = rs.getInt(3);
                if (idType == 3) {
                    idType = 2;
                }
                groupDTO.setIdType(idType);
                groupDTOList.add(groupDTO);
                groupIdContextIdMap.put(idType, groupDTOList);
            }
        } catch (Exception e) {
            knLogger.error("Exception in ", methodName, "::", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        knLogger.info(methodName, "EXIT:");
        return groupIdContextIdMap;
    }
    @Override
    public Map<String, List<String>> groupIdinContextIdMap(int groupId, List<Integer> inContextIds, String xdmsHome, KnPersisterTxn persisterTxn) {
        String methodName = "isGroupSharedToIncontext(int,List<Integer>,String,KnPersisterTxn)";
        knLogger.debug("Entry::", groupId, inContextIds);
        Connection conn = null;
        String queryForGroupId = "SELECT CORPGROUPID,ID_VALUE, ID_TYPE FROM DG.GROUP_HIERARCHY_MAP WHERE CORPGROUPID = ?";
        PreparedStatement pStmt = null;
        ResultSet rs = null;
        Map<String, List<String>> groupIdContextIdMap = new HashMap<>();
        try {
            conn = KnDBManager.getDBManagerInstance().getConnection(KnGenInfoUtil.getSecDBPttServerId(pttServerId), true);
            pStmt = conn.prepareStatement(queryForGroupId);
            pStmt.setInt(1, groupId);
            knLogger.debug(methodName, "Executing query - ", "'", queryForGroupId, "'");
            rs = pStmt.executeQuery();
            while (rs.next()) {
                groupIdContextIdMap.put(rs.getString(1), Arrays.asList(rs.getString(2).split(",")));
            }
        } catch (Exception e) {
            knLogger.error("Exception::", e);
        } finally {
            KnDbUtil.closeResultSet(rs);
            KnDbUtil.closeStatement(pStmt);
            KnDbUtil.closeConnection(conn);
        }
        knLogger.info("Exit: ", "groupIdContextIdMap:", groupIdContextIdMap);
        return groupIdContextIdMap;
    }
    @Override
    public boolean isSublistValidRequest(List<Integer> subListIdList,Map<String, Object> customParams, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        return corpListDAO.isSublistValidRequest(subListIdList,customParams, persisterTxn);
    }


    public String selectCorpFS(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpInfoDAO corpInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpInfoDAO(pttServerId);
        return corpInfoDAO.selectCorpFS(corpId, readOnly, persisterTxn);
    }

    public void updateDispMem(Collection<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpInfoDAO corpInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpInfoDAO(pttServerId);
        corpInfoDAO.updateDispMem(mdnList, persisterTxn);
    }

    /**
     * This method will update the locawatcher mdn.
     *
     * @param mdn
     * @param locwatcher
     * @param persisterTxn
     * @throws KnCorpBOException
     */
    public void updateLocwatcherMdn(String mdn, int locwatcher, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpContactListDAO contactListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpContactListDAO(pttServerId);
        contactListDAO.updateLocwatcherMdn(mdn,locwatcher, persisterTxn);
    }

    public List<String> excludeAsyncDeletionInProgressMdns(List<String> mdns, KnPersisterTxn persisterTxn) {
        String methodName = "removeAsyncDeletionInProgressMdns";
        if (mdns == null || mdns.isEmpty()) {
            return Collections.emptyList();
        }
        knLogger.debug(methodName, "Validating MDNs service auth status : ", mdns);

        try {
            KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);

            // Fetch MDN-to-authStatus map
            Map<String, Integer> subscriberServiceAuthStatusMap = subscriberTable.getSubscriberServiceAuthStatus(mdns, true, persisterTxn);
            if (subscriberServiceAuthStatusMap == null || subscriberServiceAuthStatusMap.isEmpty()) {
                return mdns;
            }
            int asyncDeletionStatus = com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_AUTH_STATUS.MARKED_FOR_ASYNC_DELETION.value();
            List<String> filteredMdns = subscriberServiceAuthStatusMap.entrySet().stream()
                    .filter(entry -> entry.getValue() == asyncDeletionStatus)
                    .map(entry -> entry.getKey().strip())
                    .collect(Collectors.toList());
            knLogger.debug(methodName, "MDNs marked for async deletion: ", filteredMdns);
            return mdns.stream()
                    .map(String::strip)
                    .filter(mdn -> !filteredMdns.contains(mdn))
                    .toList();
        } catch (KnDAOException e) {
            knLogger.error(methodName, "Error while fetching MDNs: ", e);
            return mdns; // Return the original list in case of an error
        }
    }


    public Integer getTotalMemberCountFromGroupList(List<Integer> groupIds, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getTotalMemberCountFromGroupList(List<Integer> ,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupMemberListDAO grpMemListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return grpMemListDAO.getTotalMemberCountFromGroupList(groupIds,readOnly, persisterTxn);
    }

    public Map<String, Integer> getPaginatedSubscriberData(Integer groupId , Integer firstIndex, Integer lastIndex, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPaginatedSubscriberData(Integer, Integer, Integer, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupMemberListDAO grpMemListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return grpMemListDAO.getPaginatedSubscriberData(groupId,firstIndex,lastIndex,readOnly,persisterTxn);
    }

    public Integer getGroupIdBasedonRowNumber(List<String> mdnList,int rowNumber, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupIdBasedonRowNumber(List<String>, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupMemberListDAO grpMemListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return grpMemListDAO.getGroupIdBasedonRowNumber(mdnList,rowNumber,readOnly,persisterTxn);
    }

    public Map<String,Integer> getPaginatedContatInfo(List<String> contactMdns, int startIndex, int endIndex, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        return corpListDAO.getPaginatedContatInfo(contactMdns,startIndex,endIndex,readOnly, persisterTxn);
    }

    public Integer getUniqueContactListCount(String contactMdns,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpListInfoDAO corpListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpListInfoDAO(pttServerId);
        return corpListDAO.getUniqueContactListCount(contactMdns,readOnly, persisterTxn);
    }

    @Override
    public Map<String, Integer> getPaginatedAuthUserList(List<String> targetMdnList,int startIndex, int endIndex, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getPaginatedAuthUserList(targetMdnList,startIndex,endIndex, readOnly, persisterTxn);
    }

    @Override
    public Map<String,Integer> getPaginatedDestinationOwnerMdns(List<String> destinationMdnList, int startIndex, int endIndex, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpMcpttInfoDAO mcpttInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpMcpttInfoDAO(pttServerId);
        return mcpttInfoDAO.getPaginatedDestinationOwnerMdns(destinationMdnList,startIndex,endIndex,readOnly,persisterTxn);
    }

    public Map<String,KnCorpGroupMemberDTO> getGroupSubsBasicInfo(List<Integer> groupIdList, List<String> mdnList,boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupIdBasedonRowNumber(List<String>, int, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "Entry Point :");
        KnXDMCorpGroupMemberListDAO grpMemListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpGroupMemberListDAO(pttServerId);
        return grpMemListDAO.getGroupSubsBasicInfo(groupIdList,mdnList,readOnly,persisterTxn);
    }

    /**
     * This method deleted the contactlist entries for mdnList.
     *
     * @param mdnList
     * @param persisterTxn
     * @throws KnDAOException
     */
    public void deleteCorpContactMdnList(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpContactListDAO contactListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMCorpContactListDAO(pttServerId);
        contactListDAO.deleteCorpContactMdnList(mdnList, persisterTxn);
    }

    public List<Integer> getUpmSharedCorpIds(String userProfileId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpGroupInfoDAO groupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        return groupInfoDAO.getUpmSharedCorpIds(userProfileId, xdmsHome, readOnly, persisterTxn);
    }

    public Collection<String> getMcidsByMdnList(Collection<String> membersList,
                                                KnPersisterTxn persisterTxn, boolean readOnly) throws KnDAOException {
        final String methodName = "getMcidsByMdnList(Collection<String>, KnPersisterTxn, boolean) ";
        Collection<String> subsList = new ArrayList<>();
        if (membersList == null || membersList.isEmpty()) {
            knLogger.info(methodName, "ENTRY: requested mdnlist is empty");
            return subsList;
        }
        knLogger.info(methodName, "ENTRY: requested mdn size - ", membersList.size());
        KnXDMSubscriberInfoDAO subscriberTable = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        var splittedMdnsList = KnGeneralUtil.splitList(new ArrayList<>(membersList), BULK_UPDATE_SIZE);
        for (var eachSplittedMdns : splittedMdnsList) {
            var eachSplittedSubsList = subscriberTable.getMcidsByMdnList(eachSplittedMdns, persisterTxn, readOnly);
            if (eachSplittedSubsList != null && !eachSplittedSubsList.isEmpty()) {
                subsList.addAll(eachSplittedSubsList);
            }
        }
        knLogger.info(methodName, "EXIT Point. subsList size- ", subsList.size());
        return subsList;
    }

    @Override
    public List<Integer> getMcxGroups(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        return corpGroupInfoDAO.getMCXGroups(groupIds, persisterTxn);
    }

    public Map<Integer,String> getFanDetailsByCorporateId(int corpId, KnPersisterTxn persisterTxn, boolean readyOnly) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getFanDetailsByCorporateId(corpId, persisterTxn, readyOnly);
    }

    public Map<Integer,String> getBanDetailsByCorporateId(int corpId, KnPersisterTxn persisterTxn, boolean readyOnly) throws KnDAOException {
        KnXDMSubscriberInfoDAO pocSubscInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return pocSubscInfoDAO.getBanDetailsByCorporateId(corpId, persisterTxn, readyOnly);
    }

    @Override
    public List<String> getProfileMdnsByMcids(Collection<String> mdnList, int dispMemCheck, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpContactListDAO corpContactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        return corpContactListDAO.getProfileMdnsByMcids(mdnList, dispMemCheck, persisterTxn);
    }

    @Override
    public void createPTTSettingDoc(String pttSettingId, KnCorpPTTSettingDTO pttSettingDoc, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "createPTTSettingDoc()";
        knLogger.debug(methodName,"Entry pttSettingDoc:",pttSettingDoc);
        KnPTTSettingDAO pttSettingDAO = new KnPTTSettingDAO(pttServerId);
        pttSettingDAO.createPTTSettingDoc(pttSettingId, pttSettingDoc);
        knLogger.debug(methodName, "Exit");
    }

    @Override
    public List<KnPTTSettingDocInfoDTO> getPTTSettingListById(List<KnCorpPTTSettingDocRespDTO> respDTOList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPTTSettingListById()";
        knLogger.debug(methodName,"Entry ptt Ids:",respDTOList);
        KnPTTSettingDAO pttSettingDAO = new KnPTTSettingDAO(pttServerId);
        return pttSettingDAO.getPTTSettingById(respDTOList);
    }
    @Override
    public Set<KnCorpPTTSettingDocRespDTO> getPTTSettingDocIdsForCorp(int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPTTSettingDocIdsForCorp()";
        knLogger.debug(methodName,"Entry corpId:",corpId, ", hierarchyId:",hierarchyId);
        Set<KnCorpPTTSettingDocRespDTO> pttIdList = new HashSet<>();
        KnCorpPTTSettingDocDAO pttSettingDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpPTTSettingDAO(pttServerId);
        if(hierarchyId !=null && !hierarchyId.isEmpty()){
            pttIdList =pttSettingDAO.getPTTSettingDocIdsForHierarchy(corpId, hierarchyId, persisterTxn);
        }else
            pttIdList = pttSettingDAO.getPTTSettingDocIdsForHierarchy(corpId, String.valueOf(corpId), persisterTxn);

        return pttIdList;
    }
    @Override
    public KnCorpPTTSettingDTO getPTTSettingDoc(String docId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPTTSettingDoc()";
        knLogger.debug(methodName,"Entry ptt Id:",docId);
        KnPTTSettingDAO pttSettingDAO = new KnPTTSettingDAO(pttServerId);
        return pttSettingDAO.getPTTSettingDoc(docId);
    }
    @Override
    public String getPTTSettingDocName(String docName, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPTTSettingDocName()";
        knLogger.debug(methodName,"Entry ptt name:",docName);
        KnPTTSettingDAO pttSettingDAO = new KnPTTSettingDAO(pttServerId);
        return pttSettingDAO.getPTTSettingDocName(docName);
    }

    @Override
    public void setDefaultPttSettingDoc(String docId, int corpId, String hierarchyId ,KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "setDefaultPttSettingDoc()";
        knLogger.debug(methodName,"Entry: docId - ", docId, ", corpId - ", corpId, ", hierarchyId - ", hierarchyId);
        KnCorpPTTSettingDocDAO pttSettingDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpPTTSettingDAO(pttServerId);
        pttSettingDAO.resetDefaultPTTSettingDoc(corpId, hierarchyId,persisterTxn);
        pttSettingDAO.updatePttSettingDocToDefault(docId,corpId, hierarchyId,persisterTxn);
        knLogger.debug(methodName,"Exit: Successfully delegated to updatePttSettingDocToDefault");
    }

    @Override
    public void deletePTTSettingDocFromCB(String docId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "deletePTTSettingDocFromCB()";
        knLogger.debug(methodName,"Entry ptt Id:",docId);
        KnPTTSettingDAO pttSettingDAO = new KnPTTSettingDAO(pttServerId);
        pttSettingDAO.deletePTTSettingDoc(docId);
    }
    @Override
    public List<KnPTTSettingDocInfoDTO> getAllPTTSettingDocs(String docType, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getAllPTTSettingDocs()";
        knLogger.debug(methodName,"Entry doc type:",docType);
        KnPTTSettingDAO pttSettingDAO = new KnPTTSettingDAO(pttServerId);
        return pttSettingDAO.getAllPTTSettingDocs(docType);
    }

    @Override
    public void assignPttSettingToHierarchy(KnXDMPTTSettingHierarchyListDTO pttSettingDocList, int corpId, String sysDefaultTemplate,KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "assignPttSettingToHierarchy()";
        knLogger.debug(methodName,"Entry Hierarchy:",pttSettingDocList.getHierarchyId());
        KnCorpPTTSettingDocDAO pttSettingDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpPTTSettingDAO(pttServerId);
        for(String pttDocId : pttSettingDocList.getPttSettingDocList()){
            // Skipping the System default template from assignments
            if(pttDocId.equals(sysDefaultTemplate)){
                knLogger.debug(methodName,"Skipping the System default template from assignments:",pttDocId," ,hierarchyId -",pttSettingDocList.getHierarchyId());
                continue;
            }
            knLogger.debug(methodName,"Adding mapping for docId:",pttDocId);
            pttSettingDAO.addPttSettingToHierarchy(pttDocId, corpId, pttSettingDocList.getHierarchyId(), persisterTxn);
        }
    }

    @Override
    public void unassignPttSettingToHierarchy(KnXDMPTTSettingHierarchyListDTO pttSettingDocList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "unassignPttSettingToHierarchy()";
        knLogger.debug(methodName,"Entry Hierarchy:",pttSettingDocList.getHierarchyId());
        KnCorpPTTSettingDocDAO pttSettingDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpPTTSettingDAO(pttServerId);
        String hierarchyId = pttSettingDocList.getHierarchyId();
        for(String pttDocId : pttSettingDocList.getPttSettingDocList()){
            knLogger.debug(methodName,"Removing the docId mapping:",pttDocId);
            // Unassign PTT_SETTING_DOCID if assigned for subscribers in this hierarchy that had this template
            pttSettingDAO.clearPttSettingDocFromHierarchySubscribers(pttDocId, corpId, hierarchyId, persisterTxn);
            pttSettingDAO.deletePttSettingHierarchyMapping(pttDocId, corpId, hierarchyId, persisterTxn);
        }


    }

    @Override
    public void assignPttSettingDocToMdns(List<String> mdnList, String pttSettingId, int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "assignPttSettingToHierarchy()";
        knLogger.debug(methodName,"Entry mdnList:",mdnList,", pttSettingId:",pttSettingId);
        KnCorpPTTSettingDocDAO pttSettingDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpPTTSettingDAO(pttServerId);
        pttSettingDAO.assignPttSettingDocToMdns(mdnList, pttSettingId, corpId, hierarchyId, persisterTxn);
    }

    @Override
    public void unassignPttSettingDocToMdns(List<String> mdnList, int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "unassignPttSettingToHierarchy()";
        knLogger.debug(methodName,"Entry mdnList:",mdnList);
        KnCorpPTTSettingDocDAO pttSettingDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpPTTSettingDAO(pttServerId);
        pttSettingDAO.unassignPttSettingDocToMdns(mdnList, corpId, hierarchyId, persisterTxn);
    }

    @Override
    public void updateLastProfileUpdateTimeForMdns(List<String> mdnList, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateLastProfileUpdateTimeForMdns()";
        knLogger.debug(methodName, "Entry mdnList:", mdnList);
        KnCorpPTTSettingDocDAO pttSettingDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpPTTSettingDAO(pttServerId);
        pttSettingDAO.updateLastProfileUpdateTimeForMdns(mdnList, persisterTxn);
    }

    @Override
    public List<KnXDMMdnInfoDTO> getPttSettingDocMdnList(String pttSettingId, String hierarchyId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getPttSettingDocMdnList()";
        knLogger.debug(methodName,"Entry corpId:",corpId, ", hierarchyId:",hierarchyId, ", pttSettingId:",pttSettingId);
        KnCorpPTTSettingDocDAO pttSettingDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpPTTSettingDAO(pttServerId);
        return pttSettingDAO.getPttSettingDocMdnList(pttSettingId, hierarchyId, corpId, persisterTxn);
    }

    @Override
    public int getMDNCountForPttSettingDocID(String pttSettingID, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMDNCountForPttSettingDocID()";
        knLogger.debug(methodName,"Entry pttSettingID:",pttSettingID);
        KnCorpPTTSettingDocDAO pttSettingDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpPTTSettingDAO(pttServerId);
        return pttSettingDAO.getMDNCountForPttSettingDocID(pttSettingID, corpId, persisterTxn);
    }

    @Override
    public void updateSubscriberPttSettingDocId(String newPttSettingDocId, String mdn, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updateSubscriberPttSettingDocId()";
        knLogger.debug(methodName, "Entry newPttSettingDocId:", newPttSettingDocId, ", mdn:", mdn);
        KnCorpPTTSettingDocDAO pttSettingDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpPTTSettingDAO(pttServerId);
        pttSettingDAO.updateSubscriberPttSettingDocId(newPttSettingDocId, mdn, persisterTxn);
    }

    @Override
    public void assignPttSettingToCorp(String pttSettingDocId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "assignPttSettingToCorp()";
        knLogger.debug(methodName, "Entry docId:", pttSettingDocId);
        KnCorpPTTSettingDocDAO pttSettingDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpPTTSettingDAO(pttServerId);

        pttSettingDAO.addPttSettingToHierarchy(pttSettingDocId, corpId, String.valueOf(corpId), persisterTxn);

    }
    @Override
    public void unassignPttSettingToCorp(String pttSettingDocId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "unassignPttSettingToCorp()";
        knLogger.debug(methodName, "Entry docId:", pttSettingDocId);
        KnCorpPTTSettingDocDAO pttSettingDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpPTTSettingDAO(pttServerId);

        pttSettingDAO.deletePttSettingCorpMapping(pttSettingDocId, corpId, persisterTxn);

    }

    @Override
    public List<KnCorpPTTSettingDocRespDTO> getCorpPTTSettingDocInfoForTemplateId(String pttSettingDocId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getCorpPTTSettingDocInfoForTemplateId()";
        knLogger.debug(methodName, "Entry docId:", pttSettingDocId);
        KnCorpPTTSettingDocDAO pttSettingDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpPTTSettingDAO(pttServerId);

        return pttSettingDAO.getCorpPTTSettingDocInfoForTemplateId(pttSettingDocId, persisterTxn);

    }


    public boolean isPttSettingDocAlreadyDefault(String pttSettingDocId,int corpId,String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "isPttSettingDocAlreadyDefault()";
        knLogger.debug(methodName, "Entry docId:", pttSettingDocId);
        KnCorpPTTSettingDocDAO pttSettingDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpPTTSettingDAO(pttServerId);

        return pttSettingDAO.isPttSettingDocAlreadyDefault(pttSettingDocId, corpId, hierarchyId, persisterTxn);

    }

    @Override
    public void getHierarchyDetails(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.getHierarchyDetails(corpId, removedHierarchy, persisterTxn);
    }

    @Override
    public void getSubscriberCountForHierarchyDeletion(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.getSubscriberCountForHierarchyDeletion(corpId, removedHierarchy, persisterTxn);
    }

    @Override
    public void getGroupCountForHierarchyId(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.getGroupCountForHierarchyId(corpId, removedHierarchy, persisterTxn);
    }

    @Override
    public Map<String, Integer> checkGroupProfilesForHierarchyDeletion(String corpId, List<String> hierarchyIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        return corpGroupInfoDAO.checkGroupProfilesForHierarchyDeletion(corpId, hierarchyIds, persisterTxn);
    }

    @Override
    public Map<String, Integer> checkSubListsForHierarchyDeletion(String corpId, List<String> hierarchyIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        return corpGroupInfoDAO.checkSubListsForHierarchyDeletion(corpId, hierarchyIds, persisterTxn);
    }

    @Override
    public Map<String, Integer> checkOSMListInfoForHierarchyDeletion(List<String> hierarchyIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        return corpGroupInfoDAO.checkOSMListInfoForHierarchyDeletion(hierarchyIds, persisterTxn);
    }

    @Override
    public Map<String, Integer> checkOSMListForHierarchyDeletion(String corpId, List<String> hierarchyIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        return corpGroupInfoDAO.checkOSMListForHierarchyDeletion(corpId, hierarchyIds, persisterTxn);
    }

    @Override
    public void deleteHierarchyDetails(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.deleteHierarchyDetails(corpId, removedHierarchy, persisterTxn);
    }

    @Override
    public void deleteHierarchyDepthDetails(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.deleteHierarchyDepthDetails(corpId, removedHierarchy, persisterTxn);
    }

    @Override
    public void deleteAnchorPocInfo(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.deleteAnchorPocInfo(corpId, removedHierarchy, persisterTxn);
    }

    @Override
    public void deleteHierarchyGeocodeMapping(String corpId, List<String> removedHierarchy, KnPersisterTxn persisterTxn) throws KnDAOException, KnXDMServerException {
        KnCorpGroupInfoDAO corpGroupInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.deleteHierarchyGeocodeMap(corpId, removedHierarchy, persisterTxn);
    }

    @Override
    public void insertIntoHierarchyDetails(List<KnHierarchyInfoDTO> hierarchyInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        hierarchyDAO.insertIntoHierarchyDetails(hierarchyInfoDTOS, persisterTxn);
    }

    @Override
    public void updateIntoHierarchyDetails(List<KnHierarchyInfoDTO> hierarchyInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        hierarchyDAO.updateIntoHierarchyDetails(hierarchyInfoDTOS, persisterTxn);
    }

    @Override
    public void insertOrUpdateIntoHierarchyDetails(List<KnHierarchyInfoDTO> hierarchyInfoDTOS, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        hierarchyDAO.insertOrUpdateHierarchyDetails(hierarchyInfoDTOS, corpId, persisterTxn);
    }

    @Override
    public void insertIntoHierarchyDepth(List<KnHierarchyDepthInfoDTO> hierarchyInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        hierarchyDAO.insertIntoHierarchyDepth(hierarchyInfoDTOS, persisterTxn);
    }

    @Override
    public void updateIntoHierarchyDepth(List<KnHierarchyDepthInfoDTO> hierarchyInfoDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        hierarchyDAO.updateIntoHierarchyDepth(hierarchyInfoDTOS, persisterTxn);
    }

    @Override
    public void insertOrUpdateIntoHierarchyDepth(List<KnHierarchyDepthInfoDTO> hierarchyInfoDTOS, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        hierarchyDAO.insertOrUpdateHierarchyDepth(hierarchyInfoDTOS, corpId, persisterTxn);
    }

    @Override
    public List<KnDeploySiteInfoDTO> getDeploySiteInfo(KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        return hierarchyDAO.getDeploySiteInfo(persisterTxn);
    }

    @Override
    public List<String> getExtIdNameInfo(List<String> extIdList, Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        return hierarchyDAO.getExtIdNameInfo(extIdList, corpId, persisterTxn);
    }

    @Override
    public List<String> getDescendantExtIds(String extId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        return hierarchyDAO.getDescendantExtIds(extId, persisterTxn);
    }

    @Override
    public void deleteFromHierarchyDetails(List<String> descendantExtIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        hierarchyDAO.deleteFromHierarchyDetails(descendantExtIds, persisterTxn);
    }

    @Override
    public void deleteFromHierarchyDepth(List<String> descendantExtIds, String parentHierarchyId, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        hierarchyDAO.deleteFromHierarchyDepth(descendantExtIds, parentHierarchyId, corpId, persisterTxn);
    }

    @Override
    public void insertIntoHierarchyGeocodeMap(List<KnCorpHierarchyGeocodeMapDTO> geocodeMapDTOS, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        hierarchyDAO.insertIntoHierarchyGeocodeMap(geocodeMapDTOS, persisterTxn);
    }

    @Override
    public void updateHierarchyAttributes(KnModifiedIdDetailsListDTO modifiedDetail, boolean isAliasUpdateRequest, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMHierarchyDAO(pttServerId);
        hierarchyDAO.updateHierarchyAttributes(modifiedDetail, isAliasUpdateRequest, persisterTxn);
    }

    @Override
    public Set<String> fetchExistingDescendantHierarchyIds(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException, SQLException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMHierarchyDAO(pttServerId);
        return hierarchyDAO.fetchAllDescendantHierarchyIds(corpId, persisterTxn);
    }

    @Override
    public void insertOrUpdateHierarchyGeoCode(Map<String,List<String>> addOrUpdateGeoCodes, int corpId,KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMHierarchyDAO(pttServerId);
        hierarchyDAO.insertOrUpdateModifyHierarchyGeocode(addOrUpdateGeoCodes,corpId,persisterTxn);
    }

    @Override
    public void removeGeocodeMapping(int corpId, Map<String, List<String>> removeGeocodes, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMHierarchyDAO(pttServerId);
        hierarchyDAO.removeGeocodeMapping(corpId,removeGeocodes,persisterTxn);
    }

    @Override
    public boolean isCorpHierarchyMapped(int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpContactListDAO corpContactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        return corpContactListDAO.isCorpHierarchyMapped(corpId, hierarchyId, persisterTxn);
    }

    @Override
    public Map<String, Integer> getClusterId(Set<String> geoCodes, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpContactListDAO corpContactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        return corpContactListDAO.getClusterId(geoCodes, persisterTxn);
    }

    @Override
    public Map<Integer, String> getPocHome(int corpId, String hierarchyId, List<Integer> clusterId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpContactListDAO corpContactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        return corpContactListDAO.getPocHome(corpId, hierarchyId, clusterId, persisterTxn);
    }

    @Override
    public void insertAnchorPocInfo(String corpId, String hierarchyId, int clusterId, String pocHome, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpContactListDAO corpContactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        corpContactListDAO.insertAnchorPocInfo(Integer.parseInt(corpId), hierarchyId, clusterId, pocHome, persisterTxn);
    }

    public void updateAnchorPocInfo(String corpId, String hierarchyId, int clusterId, String fetchedPocHome, KnPersisterTxn persisterTxn) throws KnDAOException{
        KnXDMCorpContactListDAO corpContactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        corpContactListDAO.updateAnchorPocInfo(Integer.parseInt(corpId), hierarchyId, clusterId, fetchedPocHome, persisterTxn);
    }


    @Override
    public void updatePocSubsInfoBatch(List<KnAllocatePocSubsUpdateDTO> updatePocSubs, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMCorpContactListDAO corpContactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        corpContactListDAO.updatePocSubsInfoBatch(updatePocSubs, persisterTxn);
    }

    @Override
    public String selectOpsCorpFS(int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "selectOpsCorpFS(int, boolean , KnPersisterTxn )";
        knLogger.debug(methodName, "ENTRY: ");
        KnXDMCorpContactListDAO corpContactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        return corpContactListDAO.selectOpsCorporateFS(corpId, readOnly, persisterTxn);
    }

    @Override
    public Map<String, KnCorpSubscriberDTO> getSubscriberDetails(List<String> mdnList, boolean readOnly, KnPersisterTxn
            persisterTxn) throws KnDAOException {
        KnXDMSubscriberInfoDAO subscriberInfoDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMSubscriberInfoDAO(pttServerId);
        return subscriberInfoDAO.getSubscriberDetails(mdnList, readOnly, persisterTxn);
    }

    @Override
    public Map<String, Integer> fetchAllHierarchyWithDepth(String corpId, String descendantId, KnPersisterTxn persisterTxn) throws KnDAOException{
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMHierarchyDAO(pttServerId);
        return hierarchyDAO.fetchAllHierarchyDepths(corpId,descendantId,persisterTxn);
    }

    @Override
    public KnHierarchyDepthInfoDTO getRootNodeBasedOnCorpId(Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        return hierarchyDAO.getRootNodeBasedOnCorpId(corpId, persisterTxn);
    }

    @Override
    public Map<String, KnHierarchyDepthInfoDTO> getChileNodesBasedOnHierarchyIds(List<String> hierarchyIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        return hierarchyDAO.getChileNodesBasedOnHierarchyIds(hierarchyIds, persisterTxn);
    }

    @Override
    public Map<String,String> getHierarchyIdNameList(Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        return hierarchyDAO.getHierarchyIdNameList(corpId, persisterTxn);
    }

    @Override
    public Map<String,String> getHierarchyIdNameAndIdList(Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        return hierarchyDAO.getHierarchyIdNameAndIdList(corpId, persisterTxn);
    }
    @Override
    public Map<String,String> getRootHierarchyName(Integer corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        return hierarchyDAO.getRootHierarchyName(corpId, persisterTxn);
    }

    @Override
    public Map<String, Map<Integer, Integer>> fetchClusterBasedSubscriberCount(Set<String> hierarchyIdList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        return hierarchyDAO.getSubscriberCountBasedOnHierarchyClusterId(hierarchyIdList,corpId, persisterTxn);
    }

    @Override
    public List<String> getRegionsByHierarchyId(int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        return hierarchyDAO.getRegionsByHierarchyId(corpId, hierarchyId, persisterTxn);
    }

    @Override
    public List<String> getAllRegions(int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().
                createXDMHierarchyDAO(pttServerId);
        return hierarchyDAO.getAllRegions(corpId, persisterTxn);
    }



    @Override
    public String fetchGroupPocHome(int corpGroupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "fetchGroupPocHome(int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        String pocHome = corpGroupInfoDAO.fetchGroupPocHome(corpGroupId, persisterTxn);
        knLogger.debug(methodName, "fetched pocHome successfully");
        return pocHome;
    }

    @Override
    public List<Integer> getGroupsWithNullPocHome(List<Integer> groupIds, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getGroupsWithNullPocHome(List<Integer>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        List<Integer> result = corpGroupInfoDAO.getGroupsWithNullPocHome(groupIds, persisterTxn);
        knLogger.debug(methodName, "fetched groups with null pocHome successfully, count - ", result.size());
        return result;
    }

    @Override
    public void updatePocHome(String pocHome, int clusterId, int corpGroupId, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "updatePocHome(String, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY:");
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.updatePocHome(pocHome, clusterId, corpGroupId, persisterTxn);
        knLogger.debug(methodName, "POC Home updated successfully");
    }

    @Override
    public void resetDefaultSettingDoc( int corpId, String hierarchyId ,KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "resetDefaultSettingDoc()";
        knLogger.debug(methodName,"Entry: corpId - ", corpId, ", hierarchyId - ", hierarchyId);
        KnCorpPTTSettingDocDAO pttSettingDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createCorpPTTSettingDAO(pttServerId);
        pttSettingDAO.resetDefaultPTTSettingDoc(corpId, hierarchyId,persisterTxn);

        knLogger.debug(methodName,"Exit: Successfully delegated to resetDefaultSettingDoc");
    }

    @Override
    public void updateGroupMemberCountByGroupId(Map<Integer, Integer> nonZeroMemberGroupIds, KnPersisterTxn persisterTxn) {
        String methodName = "updateGroupMemberCountByGroupId()";
        knLogger.debug(methodName, "ENTRY: nonZeroMemberGroupIds size - ", nonZeroMemberGroupIds.size());
        KnXDMCorpGroupInfoDAO corpGroupInfoDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpGroupInfoDAO(pttServerId);
        corpGroupInfoDAO.updateGroupMemberCountByGroupId(nonZeroMemberGroupIds, persisterTxn);
        knLogger.debug(methodName, "EXIT:");
    }

    @Override
    public void modifyPTTSettingTemplate(String pttSettingId, KnCorpPTTSettingDTO pttSettingDoc, KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "modifyPTTSettingTemplate()";
        knLogger.debug(methodName,"Entry pttSettingDoc:",pttSettingDoc);
        KnPTTSettingDAO pttSettingDAO = new KnPTTSettingDAO(pttServerId);
        pttSettingDAO.modifyPTTSettingTemplate(pttSettingId, pttSettingDoc);
        knLogger.debug(methodName, "Exit");
    }

    @Override
    public Long getMaxHierarchyId(KnPersisterTxn persisterTxn) throws KnDAOException {
        String methodName = "getMaxHierarchyId(KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY: Fetching maximum hierarchy ID from database");
        try {
            KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry()
                    .createXDMHierarchyDAO(pttServerId);
            Long maxId = hierarchyDAO.getMaxHierarchyId(persisterTxn);
            knLogger.debug(methodName, "EXIT: Maximum hierarchy ID = " + maxId);
            return maxId;
        } catch (Exception e) {
            knLogger.error(methodName, "Failed to fetch max hierarchy ID from database", e);
            throw new KnDAOException(KnErrorCodes.DAO.INTERNAL_ERROR, e.getMessage());
        }
    }

    @Override
    public Map<String,Map<String,Integer>> fetchMaxChildLengthAndDepth(List<String> hierarchyId, String corpId, KnPersisterTxn persistenceTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMHierarchyDAO(pttServerId);
        return hierarchyDAO.fetchMaxChildLengthAndDepth(hierarchyId, corpId,persistenceTxn);
    }

    @Override
    public Map<String,Integer> fetchMaxChildDepth(List<String> hierarchyId, String corpId, KnPersisterTxn persistenceTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMHierarchyDAO(pttServerId);
        return hierarchyDAO.fetchMaxChildDepthForAncestors(hierarchyId, corpId,persistenceTxn);
    }

    @Override
    public List<KnHierarchyDepthInfoDTO> getChildHierarchyDepthInfo(String ancestorHierarchyId, String corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        KnXDMHierarchyDAO hierarchyDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMHierarchyDAO(pttServerId);
        return hierarchyDAO.fetchAllChildDepthInfoForAncestor(ancestorHierarchyId, corpId, persisterTxn);
    }


    @Override
    public String selectAnsestorID(int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "selectAnsestorID(int corpId, String hierarchyId, KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "==>ENTRY: ");
        KnXDMCorpContactListDAO corpContactListDAO =
                KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        try {
            return corpContactListDAO.traceToRoot(corpId, hierarchyId, persisterTxn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Map<String,Set<String>> getHierarchyMappedGeocode(int corpId, String hierarchyId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "getHierarchyMappedGeocode(int corpId, String hierarchyId, KnPersisterTxn persisterTxn)";
        KnXDMCorpContactListDAO corpContactListDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMCorpContactListDAO(pttServerId);
        Set<String> hierarchyIds = new HashSet<>();
        hierarchyIds.add(hierarchyId);
        return corpContactListDAO.getHierarchyMappedGeocode(corpId, hierarchyIds, persisterTxn);
    }
}
