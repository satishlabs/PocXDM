/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.corpmgmt.business.helper;

import com.kodiak.common.commdto.request.KnXDMTalkGroupInfoDTO;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.featureset.KnFeatureSetUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.KnCorpDBTablesRegistry;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.tables.xdm.KnXDMSubscriberInfoDAO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPAuthUserPermissionInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPSubscriberInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnSubscrFeatureSetRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBulkGroupInfoPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;
import static com.kodiak.xdms.server.common.resources.KnConstants.BULK_UPDATE_SIZE;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.APP_UID_AUTH_LIST;
import static com.kodiak.common.resources.KnConstants.retryThreshold;
import static com.kodiak.xdms.server.corpmgmt.resources.KnConstants.*;

public class KnCorpSubsProvInfoUtil {

    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpSubsProvInfoUtil.class);

    public void modifySubsCorpFeatureSet(KnCorpSubscriberDTO corpSubscriberDTO,String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.modifySubsCorpFeatureSet(corpSubscriberDTO, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnSubscrFeatureSetRespDTO getSubscriberFeatureSets(int corpId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getSubscriberFeatureSets(corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnCorpSubscriberDTO> getSubscriberProfileDetails(Set<String> reqMdnList, int corpId, KnPersisterTxn
            persisterTxn, String xdmsHome) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            Map<String,KnCorpSubscriberDTO> subscriberDTOMap =  corpXdmDao.getSubscrFeatureBitDetails(reqMdnList, corpId, persisterTxn);
            if (subscriberDTOMap != null && subscriberDTOMap.size() != reqMdnList.size()) {
                List<String> invalidList = new ArrayList<>(reqMdnList);
                invalidList.removeAll(subscriberDTOMap.keySet());
                knLogger.error("getSubscriberProfileDetails", "Some subscribers not part of corporation ", KnGDPRTemplate.mdnList(invalidList));
                throw new KnCorpBOException(KnErrorCodes.BOEntity.SUBSCRIBER_DOES_NOT_BELONG_TO_CORP,
                        "Subscribers Does not belong to the corporation.");
            }
            return subscriberDTOMap;
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateSubscrProfiles(Map<String, KnCorpSubscriberDTO> updateSubscrMap, String xdmsHome, KnPersisterTxn
            persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.updateSubscrProfiles(updateSubscrMap, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateCorpSubscriber(KnIPSubscriberInfoDTO corpSubscriberDTO,List<String> profileMdns, String xdmsHome, KnPersisterTxn
            persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.updateCorpSubscriber(corpSubscriberDTO,profileMdns, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateSubscriberServiceAuthStatus(KnIPSubscriberInfoDTO corpSubscriberDTO, String xdmsHome, KnPersisterTxn
            persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.updateSubscriberServiceAuthStatus(corpSubscriberDTO, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public String getUserIdProfile(KnIPSubscriberInfoDTO corpSubscriberDTO, String xdmsHome, KnPersisterTxn
            persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getUserIdProfile(corpSubscriberDTO, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnCorpSubscriberDTO getUserProfile(KnIPSubscriberInfoDTO corpSubscriberDTO, String xdmsHome, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getUserProfile(corpSubscriberDTO, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Long> getTargetUserPermissions(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO,
                                                                     String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getTargetUserPermissions(ipAuthUserPermissionInfoDTO, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnMcpttPermissionDTO> getAuthUserPermissions(String authMdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getAuthUserPermissions(authMdn, xdmsHome, false, persisterTxn);
    }

    /**
     * Method to retrieve Auth user permissions. This is read only method.
     */
    public Map<String, KnMcpttPermissionDTO> getAuthUserPermissions(String authMdn, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getAuthUserPermissions(authMdn, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnMcpttPermissionDTO> getTargUserPermissions(String targetMdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getTargUserPermissions(targetMdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertIntoMcpttPermInfo(Collection<KnMcpttPermissionDTO> mcpttMappingDto, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.insertIntoMcpttPermInfo(mcpttMappingDto, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteFromMcpttPermInfoAuthMdn(String authMdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.deleteFromMcpttPermInfoAuthMdn(authMdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void deleteFromMcpttPermInfoAuthMdn(List<String> authMdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.deleteFromMcpttPermInfoAuthMdn(authMdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }
    public void deleteFromMcpttPermInfoTargetMdn(String targetMdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.deleteFromMcpttPermInfoTargetMdn(targetMdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteFromMcpttPermInfoTargetMdn(List<String> targetMdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.deleteFromMcpttPermInfoTargetMdn(targetMdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteFromAuthDoc(Collection<String> mdnList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.deleteFromAuthorizationInfo(mdnList, null, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Long> seleteFromAuthDoc(Collection<String> mdnList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.seleteFromAuthDoc(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertIntoAuthDoc(Collection<String> mdnList, Map<String, Long> authEtagMap, String xdmsHome,
                                  KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.insertIntoAuthDoc(mdnList, authEtagMap, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateToMcpttPermInfo(Collection<KnMcpttPermissionDTO> mcpttMappingDto, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.updateToMcpttPermInfo(mcpttMappingDto, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteFromMcpttPermInfo(String authMdn, Collection<String> targetMdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.deleteFromMcpttPermInfo(authMdn, targetMdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateAuthorizationImpactedTables(String pttServerId, String authMdn, String targetMdn,
                                                                        int corpId, KnPersisterTxn persisterTxn,
                                                                        Map<String, KnOPDirChgDTO> etagMap) throws KnCorpBOException {
        final String methodName = "updateAuthorizationImpactedTables(String, String, int, Collection<String>, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            Collection<String> authMdnList = new ArrayList<>();
            if(authMdn != null) authMdnList.add(authMdn);
            Collection<String> targetMdnList = new ArrayList<>();
            if(targetMdn != null) targetMdnList.add(targetMdn);
            Map<String, KnOPDirChgDTO> notificationMap = updateAuthImpactedTable(pttServerId, authMdnList, targetMdnList,
                    corpId, persisterTxn, etagMap);
            KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
            notificationMap = commonInfoUtil.setXapRootUri(notificationMap, pttServerId, persisterTxn);
            return notificationMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
                    " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateAuthImpactedTablesForRemovedContact(String pttServerId, int corpId, KnPersisterTxn persisterTxn,
                                                                                Map<String, KnOPDirChgDTO> etagMap) throws KnCorpBOException {
        final String methodName = "updateAuthImpactedTablesForRemovedContact(String, Collection<String>, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, "ENTRY  etagMap -", KnGDPRTemplate.mapKeyMdn(etagMap));
        try {
            //added null checks to avoid unwanted calls
            if (etagMap != null && !etagMap.isEmpty()) {
                Map<String, Collection<String>> removedContactMap = new HashMap<>();
                etagMap.entrySet().stream().filter(map -> map.getValue().getDocChgDTO() != null).forEach(map -> {
                    map.getValue().getDocChgDTO().stream().filter(doc -> doc.getRemovedContactList() != null).forEach(knOPDocChgDTO -> {
                        removedContactMap.put(map.getKey(), knOPDocChgDTO.getRemovedContactList());
                    });
                });
                knLogger.debug(methodName, "ENTRY :removedContactMap -", KnGDPRTemplate.mapKeyValueListMdn(removedContactMap));

                Map<String, Collection<String>> authMapRemove1 = new ConcurrentHashMap<>(removedContactMap); // [AU, (TU + Profile Mdns of TU)List]
                Map<String, Collection<String>> authCompleteMapRemove1 = new ConcurrentHashMap<>();
                Map<String, Collection<String>> authMapRemove2 = new HashMap<>(); // [AU, TU List] -> [TU, (AU+Profile Mdns of AU)List]
                Map<String, KnOPDirChgDTO> notificationMap = null;
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);

                Map<Collection<String>, Collection<String>> authProfileMdnsVsTargetMdnsMap = new HashMap<>();
                for(Map.Entry<String, Collection<String>> auMap:authMapRemove1.entrySet()) {
                    String au = auMap.getKey();
                    List<String> auList = corpXdmDao.getProfileMdnByBaseMdn(au, persisterTxn); //AU's profile MDN's
                    auList.add(au);
                    ArrayList<String> tuList = (ArrayList<String>) auMap.getValue();
                    authProfileMdnsVsTargetMdnsMap.put(auList, new ArrayList<>(tuList)); // [(AU + AU's profile MDN's)List , (TU)List]
                    List<String> profileMdns = corpXdmDao.getProfileMdnByBaseMdns(tuList, persisterTxn);
                    tuList.addAll(profileMdns);
                    authCompleteMapRemove1.put(au,tuList); // [(AU)List , (TU + TU's profile MDN's)List]
                }
                knLogger.debug(methodName," authCompleteMapRemove1:",KnGDPRTemplate.mapKeyValueListMdn(authCompleteMapRemove1));
                knLogger.debug(methodName," authMapRemove1:",KnGDPRTemplate.mapKeyValueListMdn(authMapRemove1));

                knLogger.debug(methodName, " authProfileMdnsVsTargetMdnsMap:", authProfileMdnsVsTargetMdnsMap);
                authProfileMdnsVsTargetMdnsMap.forEach((authList, targetList) ->
                        targetList.forEach(target -> {
                            if (authMapRemove2.get(target) != null) {
                                authMapRemove2.get(target).addAll(authList); //
                            } else {
                                authMapRemove2.put(target, new HashSet<>(authList)); // [(TU)List , (AU + AU's profile MDN's)List]
                            }
                        }));

                knLogger.debug(methodName, " authMapRemove2 :", KnGDPRTemplate.mapKeyValueListMdn(authMapRemove2));
                if (!authCompleteMapRemove1.isEmpty()) {
                    Collection<String> authMdnList = new ArrayList<>();
                    authMdnList.addAll(authMapRemove1.keySet());
                    authMdnList.addAll(authMapRemove2.keySet());

                    corpXdmDao.deleteFromMcpttPerm(authCompleteMapRemove1, persisterTxn);
                    corpXdmDao.deleteFromMcpttPerm(authMapRemove2, persisterTxn);
                    notificationMap = updateAuthImpactedTable(pttServerId, authMdnList, null, corpId, persisterTxn, etagMap);
                    notificationMap.forEach((mdn, dirChgDTO) -> {
                        if (authMapRemove1.containsKey(mdn)) {
                            dirChgDTO.getDocChgDTO().forEach(docChgDTO -> {
                                if (docChgDTO.getDocUri().contains(APP_UID_AUTH_LIST) && docChgDTO.getDocumentChgType() == KnConstants.DOC_CHANGE_TYPE.REPLACE.value()) {
                                    docChgDTO.setRemovedTargetList(authMapRemove1.get(mdn));
                                }
                            });
                        }
                        if (authMapRemove2.containsKey(mdn)) {
                            dirChgDTO.getDocChgDTO().forEach(docChgDTO -> {
                                if (docChgDTO.getDocUri().contains(APP_UID_AUTH_LIST) && docChgDTO.getDocumentChgType() == KnConstants.DOC_CHANGE_TYPE.REPLACE.value()) {
                                    docChgDTO.setRemovedTargetList(authMapRemove2.get(mdn));
                                }
                            });
                        }
                    });
                    KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
                    notificationMap = commonInfoUtil.setXapRootUri(notificationMap, pttServerId, persisterTxn);
                    knLogger.debug(methodName, "notificationMap :", KnGDPRTemplate.mapKeyMdn(notificationMap));
                    return notificationMap;
                }
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
                    " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return etagMap;
    }

    public Map<String,Object> updateAuthImpactedTablesForRemovedContactClone(String pttServerId, int corpId, KnPersisterTxn persisterTxn,
                                                                                Map<String, KnOPDirChgDTO> etagMap) throws KnCorpBOException {
        final String methodName = "updateAuthImpactedTablesForRemovedContactClone(String, Collection<String>, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, "ENTRY  etagMap -", KnGDPRTemplate.mapKeyMdn(etagMap));
        Map<String,Object> resultMap = new HashMap<>();
        try {
            //added null checks to avoid unwanted calls
            if (etagMap != null && !etagMap.isEmpty()) {
                Map<String, Collection<String>> removedContactMap = new HashMap<>();
                etagMap.entrySet().stream().filter(map -> map.getValue().getDocChgDTO() != null).forEach(map -> {
                    map.getValue().getDocChgDTO().stream().filter(doc -> doc.getRemovedContactList() != null).forEach(knOPDocChgDTO -> {
                        removedContactMap.put(map.getKey(), knOPDocChgDTO.getRemovedContactList());
                    });
                });
                knLogger.debug(methodName, "ENTRY :removedContactMap -", KnGDPRTemplate.mapKeyValueListMdn(removedContactMap));

                Map<String, Collection<String>> authMapRemove1 = new ConcurrentHashMap<>(removedContactMap); // [AU, (TU + Profile Mdns of TU)List]
                Map<String, Collection<String>> authCompleteMapRemove1 = new ConcurrentHashMap<>();
                Map<String, Collection<String>> authMapRemove2 = new HashMap<>(); // [AU, TU List] -> [TU, (AU+Profile Mdns of AU)List]
                Map<String, KnOPDirChgDTO> notificationMap = null;
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);

                Map<Collection<String>, Collection<String>> authProfileMdnsVsTargetMdnsMap = new HashMap<>();
                for(Map.Entry<String, Collection<String>> auMap:authMapRemove1.entrySet()) {
                    String au = auMap.getKey();
                    List<String> auList = corpXdmDao.getProfileMdnByBaseMdn(au, persisterTxn); //AU's profile MDN's
                    auList.add(au);
                    ArrayList<String> tuList = (ArrayList<String>) auMap.getValue();
                    authProfileMdnsVsTargetMdnsMap.put(auList, new ArrayList<>(tuList)); // [(AU + AU's profile MDN's)List , (TU)List]
                    List<String> profileMdns = corpXdmDao.getProfileMdnByBaseMdns(tuList, persisterTxn);
                    tuList.addAll(profileMdns);
                    authCompleteMapRemove1.put(au,tuList); // [(AU)List , (TU + TU's profile MDN's)List]
                }
                knLogger.debug(methodName," authCompleteMapRemove1:",KnGDPRTemplate.mapKeyValueListMdn(authCompleteMapRemove1));
                knLogger.debug(methodName," authMapRemove1:",KnGDPRTemplate.mapKeyValueListMdn(authMapRemove1));

                knLogger.debug(methodName, " authProfileMdnsVsTargetMdnsMap:", authProfileMdnsVsTargetMdnsMap);
                authProfileMdnsVsTargetMdnsMap.forEach((authList, targetList) ->
                        targetList.forEach(target -> {
                            if (authMapRemove2.get(target) != null) {
                                authMapRemove2.get(target).addAll(authList); //
                            } else {
                                authMapRemove2.put(target, new HashSet<>(authList)); // [(TU)List , (AU + AU's profile MDN's)List]
                            }
                        }));

                knLogger.debug(methodName, " authMapRemove2 :", KnGDPRTemplate.mapKeyValueListMdn(authMapRemove2));
                if (!authCompleteMapRemove1.isEmpty()) {
                    Collection<String> authMdnList = new ArrayList<>();
                    authMdnList.addAll(authMapRemove1.keySet());
                    authMdnList.addAll(authMapRemove2.keySet());

                    //corpXdmDao.deleteFromMcpttPerm(authCompleteMapRemove1, persisterTxn);//upd
                    resultMap.put("authCompleteMapRemove1", authMapRemove1); // defined in KnAsyncFwkServiceUtility.authMapinofType
                    //corpXdmDao.deleteFromMcpttPerm(authMapRemove2, persisterTxn);//upd
                    resultMap.put("authMapRemove2", authMapRemove2); // defined in KnAsyncFwkServiceUtility.authMapinofType
                    notificationMap = updateAuthImpactedTableClone(pttServerId, authMdnList, null, corpId, persisterTxn, etagMap, resultMap);
                    notificationMap.forEach((mdn, dirChgDTO) -> {
                        if (authMapRemove1.containsKey(mdn)) {
                            dirChgDTO.getDocChgDTO().forEach(docChgDTO -> {
                                if (docChgDTO.getDocUri().contains(APP_UID_AUTH_LIST) && docChgDTO.getDocumentChgType() == KnConstants.DOC_CHANGE_TYPE.REPLACE.value()) {
                                    docChgDTO.setRemovedTargetList(authMapRemove1.get(mdn));
                                }
                            });
                        }
                        if (authMapRemove2.containsKey(mdn)) {
                            dirChgDTO.getDocChgDTO().forEach(docChgDTO -> {
                                if (docChgDTO.getDocUri().contains(APP_UID_AUTH_LIST) && docChgDTO.getDocumentChgType() == KnConstants.DOC_CHANGE_TYPE.REPLACE.value()) {
                                    docChgDTO.setRemovedTargetList(authMapRemove2.get(mdn));
                                }
                            });
                        }
                    });
                    KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
                    notificationMap = commonInfoUtil.setXapRootUri(notificationMap, pttServerId, persisterTxn);
                    knLogger.debug(methodName, "notificationMap :", KnGDPRTemplate.mapKeyMdn(notificationMap));
                    resultMap.put("etagMap", notificationMap);
                    //return notificationMap;
                    return resultMap;
                }
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
                    " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        resultMap.put("etagMap", etagMap);
        //return etagMap;
        return resultMap;
    }

    private Map<String, KnOPDirChgDTO> updateAuthImpactedTable(String pttServerId, Collection<String> authMdnList,
                                                               Collection<String> targetMdnList, int corpId, KnPersisterTxn persisterTxn,
                                                               Map<String, KnOPDirChgDTO> etagMap) throws KnDAOException {
        final String methodName = "updateAuthImpactedTable(String, Collection<String>,  Collection<String>, int, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        Map<String, Integer> authMdnMapCount = new HashMap<>();
        Collection<String> authMdns = null;
        if(!authMdnList.isEmpty()){
            authMdnMapCount = corpXdmDao.getAuthUserMappingCount(authMdnList, corpId, persisterTxn);
        } else {
            authMdns = corpXdmDao.getAuthMdnListFromTarget(targetMdnList, corpId, persisterTxn);
            authMdnList = new ArrayList<>(authMdns);
        }
        knLogger.debug(methodName, "ENTRY authMdnMapCount :", KnGDPRTemplate.mapKeyMdn(authMdnMapCount));
        Map<String, KnOPDirChgDTO> currdirectoryEtag = corpXdmDao.updateEtag(authMdnList, persisterTxn, etagMap);
        knLogger.debug(methodName, "ENTRY currdirectoryEtag :", currdirectoryEtag);
        Map<String, KnOPDocChgDTO> authorizationMap = new HashMap<String, KnOPDocChgDTO>();
        Collection<String> authWithoutMapping = authMdnMapCount.entrySet().stream().filter(map -> map.getValue() == 0).map(Map.Entry::getKey).collect(Collectors.toList());
        Collection<String> authWithMapping = authMdnMapCount.entrySet().stream().filter(map -> map.getValue() != 0).map(Map.Entry::getKey).collect(Collectors.toList());
        if(authMdns != null && !authMdns.isEmpty())  authWithMapping.addAll(authMdns);
        knLogger.debug(methodName, "ENTRY authWithoutMapping : ", authWithoutMapping, "authWithMapping -- ", authWithMapping);
        if(authWithoutMapping != null && !authWithoutMapping.isEmpty()) authorizationMap = corpXdmDao.deleteFromAuthorizationInfo(authWithoutMapping, authorizationMap, persisterTxn);
        if(authWithMapping != null && !authWithMapping.isEmpty()) authorizationMap = corpXdmDao.insertOrUpdateAuthorizationInfo(authWithMapping, authorizationMap, persisterTxn);
        knLogger.debug(methodName, "ENTRY authorizationMap :", authorizationMap);
        KnXDMSubscriberInfoDAO subscriberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        Map<String, KnCorpSubscriberDTO> subscrInfoMap = subscriberDAO.getSubsribersCorporateDetails(authMdnList, persisterTxn);
        for (String mdn : currdirectoryEtag.keySet()) {
            Collection<KnOPDocChgDTO> docLists = null;
            mdn = mdn.trim();
            KnOPDirChgDTO directory = currdirectoryEtag.get(mdn);
            if(directory.getDocChgDTO() != null && !directory.getDocChgDTO().isEmpty()){
                docLists = directory.getDocChgDTO();
                if(authorizationMap.get(mdn) != null){
                    docLists.add(authorizationMap.get(mdn));
                }
            } else {
                docLists = new ArrayList<>();
                if(authorizationMap.get(mdn) != null){
                    docLists.add(authorizationMap.get(mdn));
                }
            }
            directory.setDocChgDTO(docLists);
            KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
            if (subsc != null) {
                directory.setPocHome(subsc.getPocHome());
                directory.setPresenceHome(subsc.getPresenceHome());
                directory.setNotfnCapability(subsc.isNotfnCapabiliy());
                directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                directory.setClientType(subsc.getClientType());
            }
        }
        List<String> tempList = new ArrayList<>(authMdnList);
        tempList.removeAll(currdirectoryEtag.keySet());
        knLogger.debug(methodName, "EXIT:Mismatched mdnList for whom DocDiff notification could not be sent", KnGDPRTemplate.mdnList(tempList));
        knLogger.debug(methodName, "ENTRY etagMap :", KnGDPRTemplate.mapKeyMdn(currdirectoryEtag));
        return currdirectoryEtag;
    }

    private Map<String, KnOPDirChgDTO> updateAuthImpactedTableForUpm(String pttServerId, Collection<String> authMdnList,
                                                               Collection<String> targetMdnList, int corpId, KnPersisterTxn persisterTxn,
                                                               Map<String, KnOPDirChgDTO> currdirectoryEtag) throws KnDAOException {
        final String methodName = "updateAuthImpactedTableForUpm(String, Collection<String>,  Collection<String>, int, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        Map<String, Integer> authMdnMapCount = new HashMap<>();
        Collection<String> authMdns = null;
        if(!authMdnList.isEmpty()){
            authMdnMapCount = corpXdmDao.getAuthUserMappingCount(authMdnList, corpId, persisterTxn);
        } else {
            authMdns = corpXdmDao.getAuthMdnListFromTarget(targetMdnList, corpId, persisterTxn);
            authMdnList = new ArrayList<>(authMdns);
        }
        knLogger.debug(methodName, "ENTRY authMdnMapCount :", KnGDPRTemplate.mapKeyMdn(authMdnMapCount));
        //Map<String, KnOPDirChgDTO> currdirectoryEtag = corpXdmDao.updateEtag(authMdnList, persisterTxn, etagMap);
        knLogger.debug(methodName, "ENTRY currdirectoryEtag :", currdirectoryEtag);
        Map<String, KnOPDocChgDTO> authorizationMap = new HashMap<String, KnOPDocChgDTO>();
        Collection<String> authWithoutMapping = authMdnMapCount.entrySet().stream().filter(map -> map.getValue() == 0).map(Map.Entry::getKey).collect(Collectors.toList());
        Collection<String> authWithMapping = authMdnMapCount.entrySet().stream().filter(map -> map.getValue() != 0).map(Map.Entry::getKey).collect(Collectors.toList());
        if(authMdns != null && !authMdns.isEmpty())  authWithMapping.addAll(authMdns);
        knLogger.debug(methodName, "ENTRY authWithoutMapping : ", authWithoutMapping, "authWithMapping -- ", authWithMapping);
        if(authWithoutMapping != null && !authWithoutMapping.isEmpty()) authorizationMap = corpXdmDao.deleteFromAuthorizationInfo(authWithoutMapping, authorizationMap, persisterTxn);
        if(authWithMapping != null && !authWithMapping.isEmpty()) authorizationMap = corpXdmDao.insertOrUpdateAuthorizationInfo(authWithMapping, authorizationMap, persisterTxn);
        knLogger.debug(methodName, "ENTRY authorizationMap :", authorizationMap);
        KnXDMSubscriberInfoDAO subscriberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        Map<String, KnCorpSubscriberDTO> subscrInfoMap = subscriberDAO.getSubsribersCorporateDetails(authMdnList, persisterTxn);
        for (String mdn : currdirectoryEtag.keySet()) {
            Collection<KnOPDocChgDTO> docLists = null;
            mdn = mdn.trim();
            KnOPDirChgDTO directory = currdirectoryEtag.get(mdn);
            if(directory.getDocChgDTO() != null && !directory.getDocChgDTO().isEmpty()){
                docLists = directory.getDocChgDTO();
                if(authorizationMap.get(mdn) != null){
                    docLists.add(authorizationMap.get(mdn));
                }
            } else {
                docLists = new ArrayList<>();
                if(authorizationMap.get(mdn) != null){
                    docLists.add(authorizationMap.get(mdn));
                }
            }
            directory.setDocChgDTO(docLists);
            KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
            if (subsc != null) {
                directory.setPocHome(subsc.getPocHome());
                directory.setPresenceHome(subsc.getPresenceHome());
                directory.setNotfnCapability(subsc.isNotfnCapabiliy());
                directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                directory.setClientType(subsc.getClientType());
            }
        }
        List<String> tempList = new ArrayList<>(authMdnList);
        tempList.removeAll(currdirectoryEtag.keySet());
        knLogger.debug(methodName, "EXIT:Mismatched mdnList for whom DocDiff notification could not be sent", KnGDPRTemplate.mdnList(tempList));
        knLogger.debug(methodName, "ENTRY etagMap :", KnGDPRTemplate.mapKeyMdn(currdirectoryEtag));
        return currdirectoryEtag;
    }

    private void updateAuthTable(String pttServerId, Collection<String> authMdnList,
                                 Collection<String> targetMdnList, int corpId, KnPersisterTxn persisterTxn) throws KnDAOException {
        final String methodName = "updateAuthTable(String, Collection<String>,  Collection<String>, int, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        Map<String, Integer> authMdnMapCount = new HashMap<>();
        Collection<String> authMdns = null;
        if(!authMdnList.isEmpty()){
            authMdnMapCount = corpXdmDao.getAuthUserMappingCount(authMdnList, corpId, persisterTxn);
        } else {
            authMdns = corpXdmDao.getAuthMdnListFromTarget(targetMdnList, corpId, persisterTxn);
            authMdnList = new ArrayList<>(authMdns);
        }
        knLogger.debug(methodName, "ENTRY authMdnMapCount :", KnGDPRTemplate.mapKeyMdn(authMdnMapCount));
        Map<String, KnOPDocChgDTO> authorizationMap = new HashMap<String, KnOPDocChgDTO>();
        Collection<String> authWithoutMapping = authMdnMapCount.entrySet().stream().filter(map -> map.getValue() == 0).map(Map.Entry::getKey).collect(Collectors.toList());
        Collection<String> authWithMapping = authMdnMapCount.entrySet().stream().filter(map -> map.getValue() != 0).map(Map.Entry::getKey).collect(Collectors.toList());
        if(authMdns != null && !authMdns.isEmpty())  authWithMapping.addAll(authMdns);
        knLogger.debug(methodName, "ENTRY authWithoutMapping : ", authWithoutMapping, "authWithMapping -- ", authWithMapping);
        if(authWithoutMapping != null && !authWithoutMapping.isEmpty()) authorizationMap = corpXdmDao.deleteFromAuthorizationInfo(authWithoutMapping, authorizationMap, persisterTxn);
        if(authWithMapping != null && !authWithMapping.isEmpty()) authorizationMap = corpXdmDao.insertOrUpdateAuthorizationInfo(authWithMapping, authorizationMap, persisterTxn);
        knLogger.debug(methodName, "ENTRY authorizationMap :", authorizationMap);
    }
    private Map<String, KnOPDirChgDTO> updateAuthImpactedTableClone(String pttServerId, Collection<String> authMdnList,
                                                               Collection<String> targetMdnList, int corpId, KnPersisterTxn persisterTxn,
                                                               Map<String, KnOPDirChgDTO> etagMap, Map<String,Object> resultMap) throws KnDAOException {
        final String methodName = "updateAuthImpactedTableClone(String, Collection<String>,  Collection<String>, int, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        Map<String, Integer> authMdnMapCount = new HashMap<>();
        Collection<String> authMdns = null;
        if(!authMdnList.isEmpty()){
            authMdnMapCount = corpXdmDao.getAuthUserMappingCount(authMdnList, corpId, persisterTxn);
        } else {
            authMdns = corpXdmDao.getAuthMdnListFromTarget(targetMdnList, corpId, persisterTxn);
            authMdnList = new ArrayList<>(authMdns);
        }
        knLogger.debug(methodName, "ENTRY authMdnMapCount :", KnGDPRTemplate.mapKeyMdn(authMdnMapCount));
        Map<String, KnOPDirChgDTO> currdirectoryEtag = corpXdmDao.selectEtag(authMdnList, persisterTxn, etagMap);
        //knLogger.debug(methodName, "ENTRY currdirectoryEtag :", currdirectoryEtag);
        Map<String, KnOPDocChgDTO> authorizationMap = new HashMap<String, KnOPDocChgDTO>();
        Collection<String> authWithoutMapping = authMdnMapCount.entrySet().stream().filter(map -> map.getValue() == 0).map(Map.Entry::getKey).collect(Collectors.toList());
        Collection<String> authWithMapping = authMdnMapCount.entrySet().stream().filter(map -> map.getValue() != 0).map(Map.Entry::getKey).collect(Collectors.toList());
        if(authMdns != null && !authMdns.isEmpty())  authWithMapping.addAll(authMdns);
        knLogger.debug(methodName, "ENTRY authWithoutMapping : ", authWithoutMapping, "authWithMapping -- ", authWithMapping);
        if (!authWithoutMapping.isEmpty()) {
            //authorizationMap = corpXdmDao.deleteFromAuthorizationInfo(authWithoutMapping, authorizationMap, persisterTxn);
            resultMap.put("authWithoutMapping", authWithoutMapping);
        }
        if (!authWithMapping.isEmpty()) {
            //authorizationMap = corpXdmDao.insertOrUpdateAuthorizationInfo(authWithMapping, authorizationMap, persisterTxn);
            resultMap.put("authWithMapping", authWithMapping);
        }
        knLogger.debug(methodName, "ENTRY authorizationMap :", authorizationMap);
        KnXDMSubscriberInfoDAO subscriberDAO = KnCorpDBTablesRegistry.getDBXdmTableRegistry().createXDMSubscriberInfoDAO(pttServerId);
        Map<String, KnCorpSubscriberDTO> subscrInfoMap = subscriberDAO.getSubsribersCorporateDetails(authMdnList, persisterTxn);
        for (String mdn : currdirectoryEtag.keySet()) {
            Collection<KnOPDocChgDTO> docLists = null;
            mdn = mdn.trim();
            KnOPDirChgDTO directory = currdirectoryEtag.get(mdn);
            if(directory.getDocChgDTO() != null && !directory.getDocChgDTO().isEmpty()){
                docLists = directory.getDocChgDTO();
                if(authorizationMap.get(mdn) != null){
                    docLists.add(authorizationMap.get(mdn));
                }
            } else {
                docLists = new ArrayList<>();
                if(authorizationMap.get(mdn) != null){
                    docLists.add(authorizationMap.get(mdn));
                }
            }
            directory.setDocChgDTO(docLists);
            KnCorpSubscriberDTO subsc = subscrInfoMap.get(mdn);
            if (subsc != null) {
                directory.setPocHome(subsc.getPocHome());
                directory.setPresenceHome(subsc.getPresenceHome());
                directory.setNotfnCapability(subsc.isNotfnCapabiliy());
                directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                directory.setClientType(subsc.getClientType());
            }
        }
        List<String> tempList = new ArrayList<>(authMdnList);
        tempList.removeAll(currdirectoryEtag.keySet());
        knLogger.debug(methodName, "EXIT:Mismatched mdnList for whom DocDiff notification could not be sent", KnGDPRTemplate.mdnList(tempList));
        knLogger.debug(methodName, "ENTRY etagMap :", KnGDPRTemplate.mapKeyMdn(currdirectoryEtag));
        return currdirectoryEtag;
    }

    public void integerToBitsConversion(BitSet bitSet, KnTargetMdnPermBitInfo targetMdnPermBitInfo){
        final String methodName = "integerToBitsConversion(BitSet, KnTargetMdnPermBitInfo)";
        knLogger.debug(methodName, "ENTRY :");
        bitSet.set(KnConstants.MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value(), targetMdnPermBitInfo.getAmbientListening() == 1);
        bitSet.set(KnConstants.MCPTT_PERMISSION_BIT.DISCRETELISTENING.value(), targetMdnPermBitInfo.getDiscreteListening() == 1);
        bitSet.set(KnConstants.MCPTT_PERMISSION_BIT.USERCHECK.value(), targetMdnPermBitInfo.getUserCheck() == 1);
        bitSet.set(KnConstants.MCPTT_PERMISSION_BIT.USERENABLE.value(), targetMdnPermBitInfo.getUserEnable() == 1);
        bitSet.set(KnConstants.MCPTT_PERMISSION_BIT.REMOTEEMERGENCYPERMISSION.value(), targetMdnPermBitInfo.getEmergPermission() == 1);
        bitSet.set(KnConstants.MCPTT_PERMISSION_BIT.MCVIDEOUNCONFIRMEDPULL.value(), targetMdnPermBitInfo.getMcVideoUnConfirmedPull() == 1);
        knLogger.debug(methodName, "EXIT :");
    }

    public void bitsToIntegerConversion(BitSet bitSet, KnTargetMdnPermBitInfo targetMdnPermBitInfo){
        final String methodName = "bitsToIntegerConversion(BitSet, KnTargetMdnPermBitInfo)";
        knLogger.debug(methodName, "ENTRY :");
        targetMdnPermBitInfo.setAmbientListening(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.AMBIENTLISTENING.value()) ? 1 : 0);
        targetMdnPermBitInfo.setDiscreteListening(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.DISCRETELISTENING.value()) ? 1 : 0);
        targetMdnPermBitInfo.setUserCheck(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.USERCHECK.value()) ? 1 : 0);
        targetMdnPermBitInfo.setUserEnable(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.USERENABLE.value()) ? 1 : 0);
        targetMdnPermBitInfo.setEmergPermission(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.REMOTEEMERGENCYPERMISSION.value()) ? 1 : 0);
        targetMdnPermBitInfo.setMcVideoUnConfirmedPull(bitSet.get(KnConstants.MCPTT_PERMISSION_BIT.MCVIDEOUNCONFIRMEDPULL.value()) ? 1 : 0);
        knLogger.debug(methodName, "EXIT :");
    }

    public Map<String, Integer> getAuthMdnList(KnIPAuthUserPermissionInfoDTO ipAuthUserPermissionInfoDTO, String xdmsHome, boolean readOnly,
                                             KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getAuthMdnList(ipAuthUserPermissionInfoDTO, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnSubsDestEmergencyAttributes> getEmergAttributes(String mdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getEmergAttributes(mdn, xdmsHome, false, persisterTxn);
    }

    public Collection<KnSubsDestEmergencyAttributes> getEmergAttributes(String mdn, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
           return corpXdmDao.getEmergDestAttributes(mdn, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Collection<String>> getEmergUserDestMap(Collection<String> emergUserList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getEmergUserDestMap(emergUserList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Collection<String>> getEmergDestUserMap(Collection<String> emergDestList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getEmergDestUserMap(emergDestList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnSubsDestEmergencyAttributes> getEmergDestAttributesForDestination(String destination, String xdmsHome, boolean readOnly,
                                                                                          KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getEmergDestAttributesForDestination(destination, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnSubsEmergencyAttributes getEmergSubsAttributes(String mdn, int corpId, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getEmergSubsAttributes(mdn, corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteFromEmergSubsDestInfo(String mdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.deleteFromEmergSubsDestInfo(mdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteFromEmergSubsDestInfo(List<String> mdnList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.deleteFromEmergSubsDestInfo(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteFromEmergInfoForDest(String emergDest, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.deleteFromEmergInfoForDest(emergDest, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteFromEmergInfoForDest(List<String> emergDest, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.deleteFromEmergInfoForDest(emergDest, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateToEmergSubsDestInfo(KnSubsEmergencyAttributes subsEmergencyAttributes, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            // update to pocsubsinfo
            corpXdmDao.updateToEmergSubsDestInfo(subsEmergencyAttributes, persisterTxn);
            // update to pocsubsaddinfo
            corpXdmDao.updateToEmergSubsDestAddInfo(subsEmergencyAttributes, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateToPrivacyOptStatus(Map<String,Integer> mapListForPrivacy,String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.updateToMcpttPermInfoForPrivacyStatus(mapListForPrivacy, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertIntoEmergSubsAttributes(Collection<KnSubsDestEmergencyAttributes> destEmergencyAttributes,
                                              String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.insertIntoEmergSubsAttributes(destEmergencyAttributes, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateEmergSubsAttributes(Collection<KnSubsDestEmergencyAttributes> destEmergencyAttributes,
                                          String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.updateEmergSubsAttributes(destEmergencyAttributes, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteFromEmergDoc(Collection<String> mdnList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.deleteFromEmergDoc(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Long> seleteFromEmergDoc(Collection<String> mdnList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.seleteFromEmergDoc(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertIntoEmergDoc(Collection<String> mdnList, Map<String, Long> emergEtagMap, String xdmsHome,
                                   KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.insertIntoEmergDoc(mdnList, emergEtagMap, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateEmergencyImpactedTables(String pttServerId, String mdn, int corpId, KnPersisterTxn persisterTxn,
                                                                    Map<String, KnOPDirChgDTO> etagMap) throws KnCorpBOException {
        final String methodName = "updateEmergencyImpactedTables(String, String, int, Collection<String>, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            Collection<String> mdnList = new ArrayList<>();
            if(mdn != null) mdnList.add(mdn);
            Map<String, KnOPDirChgDTO> currdirectoryEtag = corpXdmDao.updateEtag(mdnList, persisterTxn, etagMap);
            knLogger.debug(methodName, " currdirectoryEtag :", KnGDPRTemplate.mapKeyMdn(currdirectoryEtag));
            Map<String, KnOPDocChgDTO> emergencyMap = corpXdmDao.insertOrUpdateEmergencyInfo(mdnList, persisterTxn);
            knLogger.debug(methodName, " emergencyMap :", KnGDPRTemplate.mapKeyMdn(emergencyMap));
            Map<String, KnCorpSubscriberDTO> subscrInfoMap = corpXdmDao.getSubsribersCorporateDetails(mdnList, persisterTxn);
            for (String subs : currdirectoryEtag.keySet()) {
                Collection<KnOPDocChgDTO> docLists = null;
                subs = subs.trim();
                KnOPDirChgDTO directory = currdirectoryEtag.get(subs);
                if(directory.getDocChgDTO() != null && !directory.getDocChgDTO().isEmpty()){
                    docLists = directory.getDocChgDTO();
                    if(emergencyMap.get(subs) != null){
                        docLists.add(emergencyMap.get(subs));
                    }
                } else {
                    docLists = new ArrayList<>();
                    if(emergencyMap.get(subs) != null){
                        docLists.add(emergencyMap.get(subs));
                    }
                }
                directory.setDocChgDTO(docLists);
                KnCorpSubscriberDTO subsc = subscrInfoMap.get(subs);
                if (subsc != null) {
                    directory.setPocHome(subsc.getPocHome());
                    directory.setPresenceHome(subsc.getPresenceHome());
                    directory.setNotfnCapability(subsc.isNotfnCapabiliy());
                    directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                    directory.setClientType(subsc.getClientType());
                }
            }
            List<String> tempList = new ArrayList<>(mdnList);
            tempList.removeAll(currdirectoryEtag.keySet());
            knLogger.debug(methodName, "EXIT:Mismatched mdnList for whom DocDiff notification could not be sent", KnGDPRTemplate.mdnList(tempList));
            knLogger.debug(methodName, "etagMap :", currdirectoryEtag);
            KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
            currdirectoryEtag = commonInfoUtil.setXapRootUri(currdirectoryEtag, pttServerId, persisterTxn);
            return currdirectoryEtag;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
                    " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateEmergencyImpactedTablesClone(String pttServerId, List<String> mdnList, int corpId, KnPersisterTxn persisterTxn,
                                                                    Map<String, KnOPDirChgDTO> etagMap) throws KnCorpBOException {
        final String methodName = "updateEmergencyImpactedTables(String, String, int, Collection<String>, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, "ENTRY :", KnGDPRTemplate.mdnList(mdnList));
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);

            Map<String, KnOPDirChgDTO> currdirectoryEtag = corpXdmDao.selectEtag(mdnList, persisterTxn, etagMap);
            knLogger.debug(methodName, " currdirectoryEtag :", KnGDPRTemplate.mapKeyMdn(currdirectoryEtag));
            Map<String, KnOPDocChgDTO> emergencyMap = corpXdmDao.insertOrUpdateEmergencyInfo(mdnList, persisterTxn);
            knLogger.debug(methodName, " emergencyMap :", KnGDPRTemplate.mapKeyMdn(emergencyMap));
            Map<String, KnCorpSubscriberDTO> subscrInfoMap = corpXdmDao.getSubsribersCorporateDetails(mdnList, persisterTxn);
            for (String subs : currdirectoryEtag.keySet()) {
                Collection<KnOPDocChgDTO> docLists = null;
                subs = subs.trim();
                KnOPDirChgDTO directory = currdirectoryEtag.get(subs);
                if(directory.getDocChgDTO() != null && !directory.getDocChgDTO().isEmpty()){
                    docLists = directory.getDocChgDTO();
                    if(emergencyMap.get(subs) != null){
                        docLists.add(emergencyMap.get(subs));
                    }
                } else {
                    docLists = new ArrayList<>();
                    if(emergencyMap.get(subs) != null){
                        docLists.add(emergencyMap.get(subs));
                    }
                }
                directory.setDocChgDTO(docLists);
                KnCorpSubscriberDTO subsc = subscrInfoMap.get(subs);
                if (subsc != null) {
                    directory.setPocHome(subsc.getPocHome());
                    directory.setPresenceHome(subsc.getPresenceHome());
                    directory.setNotfnCapability(subsc.isNotfnCapabiliy());
                    directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                    directory.setClientType(subsc.getClientType());
                }
            }
            List<String> tempList = new ArrayList<>(mdnList);
            tempList.removeAll(currdirectoryEtag.keySet());
            knLogger.debug(methodName, "EXIT:Mismatched mdnList for whom DocDiff notification could not be sent", KnGDPRTemplate.mdnList(tempList));
            knLogger.debug(methodName, "etagMap :", currdirectoryEtag);
            KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
            currdirectoryEtag = commonInfoUtil.setXapRootUri(currdirectoryEtag, pttServerId, persisterTxn);
            return currdirectoryEtag;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
                    " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateEmergencyImpactedTablesForUpm(String pttServerId, String mdn, int corpId, KnPersisterTxn persisterTxn,
                                                                    Map<String, KnOPDirChgDTO> currdirectoryEtag) throws KnCorpBOException {
        final String methodName = "updateEmergencyImpactedTables(String, String, int, Collection<String>, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            Collection<String> mdnList = new ArrayList<>();
            if(mdn != null) mdnList.add(mdn);
            //Map<String, KnOPDirChgDTO> currdirectoryEtag = corpXdmDao.updateEtag(mdnList, persisterTxn, etagMap);
            knLogger.debug(methodName, " currdirectoryEtag :", KnGDPRTemplate.mapKeyMdn(currdirectoryEtag));
            Map<String, KnOPDocChgDTO> emergencyMap = corpXdmDao.insertOrUpdateEmergencyInfo(mdnList, persisterTxn);
            knLogger.debug(methodName, " emergencyMap :", KnGDPRTemplate.mapKeyMdn(emergencyMap));
            Map<String, KnCorpSubscriberDTO> subscrInfoMap = corpXdmDao.getSubsribersCorporateDetails(mdnList, persisterTxn);
            for (String subs : currdirectoryEtag.keySet()) {
                Collection<KnOPDocChgDTO> docLists = null;
                subs = subs.trim();
                KnOPDirChgDTO directory = currdirectoryEtag.get(subs);
                if(directory.getDocChgDTO() != null && !directory.getDocChgDTO().isEmpty()){
                    docLists = directory.getDocChgDTO();
                    if(emergencyMap.get(subs) != null){
                        docLists.add(emergencyMap.get(subs));
                    }
                } else {
                    docLists = new ArrayList<>();
                    if(emergencyMap.get(subs) != null){
                        docLists.add(emergencyMap.get(subs));
                    }
                }
                directory.setDocChgDTO(docLists);
                KnCorpSubscriberDTO subsc = subscrInfoMap.get(subs);
                if (subsc != null) {
                    directory.setPocHome(subsc.getPocHome());
                    directory.setPresenceHome(subsc.getPresenceHome());
                    directory.setNotfnCapability(subsc.isNotfnCapabiliy());
                    directory.setProtoVersion(Integer.toString(subsc.getClientPVmajorVer()));
                    directory.setClientType(subsc.getClientType());
                }
            }
            List<String> tempList = new ArrayList<>(mdnList);
            tempList.removeAll(currdirectoryEtag.keySet());
            knLogger.debug(methodName, "EXIT:Mismatched mdnList for whom DocDiff notification could not be sent", KnGDPRTemplate.mdnList(tempList));
            knLogger.debug(methodName, "etagMap :", currdirectoryEtag);
            KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
            currdirectoryEtag = commonInfoUtil.setXapRootUri(currdirectoryEtag, pttServerId, persisterTxn);
            return currdirectoryEtag;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
                    " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateDirectoryEtag(Collection<String> mdnList, String xdmsHome, Map<String, KnOPDirChgDTO> etagMap, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.updateEtag(mdnList, persisterTxn, etagMap);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public boolean isEmergencyDestExists(String emergDest, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.isEmergencyDestExists(emergDest, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public String getAliasMdnProfile(KnIPSubscriberInfoDTO corpSubscriberDTO, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getAliasMdnProfile(corpSubscriberDTO, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateSubsEntities(KnIPSubscriberInfoDTO corpSubscriberDTO, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.updateSubsEntities(corpSubscriberDTO, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnCorpSubscriberDTO> getSubscriberAdditionalDetails(Collection<String> mdnList, String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getSubscriberAdditionalDetails(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void boundaryConditionValidationPopulation(KnCorpBulkGroupInfoPersistDTO persistDTO, Map<Integer, KnCorpGroupDTO> groupDetailsMap,
                                                      Collection<Integer> toMdnGroupList) {
        final String methodName = "boundaryConditionValidationPopulation(KnCorpBulkGroupInfoPersistDTO, Map<Integer, KnCorpGroupDTO>)";
        final int[] lrgGrpCount = {0};
        final int[] lrgGrpCountBG = {0};
        Map<Integer, Integer> grpIdNormalToLrgTran = new HashMap<>();
        Map<Integer, Integer> standardGroupsCount = new HashMap<>();
        Map<Integer, Integer> dispatchGroupsCount = new HashMap<>();
        Map<Integer, Integer> broadcastGroupsCount = new HashMap<>();
        groupDetailsMap.forEach((grpId, value) -> {
            //LargeGroupCount:
            if (value.isLargeGroup()) {
                if (value.getGroupType() != BROADCAST_GROUP) {
                    lrgGrpCount[0]++;
                } else {
                    lrgGrpCountBG[0]++;
                }
            } else {
                if ((value.getGroupType() == STANDARD_GROUP && value.getGroupMemCount() + 1 >= persistDTO.getMaxNumberOfMembers())
                        || (value.getGroupType() == DISPATCH_GROUP && value.getGroupMemCount() + 1 >= persistDTO.getMaxSubscriberPerDispatchGroup())) {
                    lrgGrpCount[0]++;
                    grpIdNormalToLrgTran.put(grpId, value.getGroupType());
                } else if (value.getGroupType() == BROADCAST_GROUP && value.getGroupMemCount() + 1 >= persistDTO.getMaxAllowedMemPerBCG()) {
                    lrgGrpCountBG[0]++;
                    grpIdNormalToLrgTran.put(grpId, value.getGroupType());
                }
            }
            //GroupMemberCount:
            if(!toMdnGroupList.contains(grpId)) {
                if (value.getGroupType() == STANDARD_GROUP) {
                    standardGroupsCount.put(grpId, value.getGroupMemCount() + 1);
                } else if (value.getGroupType() == DISPATCH_GROUP) {
                    dispatchGroupsCount.put(grpId, value.getGroupMemCount() + 1);
                } else {
                    broadcastGroupsCount.put(grpId, value.getGroupMemCount() + 1);
                }
            }
        });
        knLogger.debug(methodName, "lrgGrpCount: ", lrgGrpCount, "lrgGrpCountBG: ", lrgGrpCountBG);
        knLogger.debug(methodName, "standardGroupsCount: ", standardGroupsCount, "dispatchGroupsCount: ",
                dispatchGroupsCount, "broadcastGroupsCount: ", broadcastGroupsCount);
        persistDTO.setActualMemberForLargeGroup(lrgGrpCount[0]);
        persistDTO.setActualMemberForLargeGroupBC(lrgGrpCountBG[0]);
        persistDTO.setStandardGroupsCount(standardGroupsCount);
        persistDTO.setDispatchGroupsCount(dispatchGroupsCount);
        persistDTO.setBroadcastGroupsCount(broadcastGroupsCount);
        persistDTO.setGrpIdNormalToLrgTran(grpIdNormalToLrgTran);
    }

    public void updateSubsProfileLastUpdateTime(KnCorpSubscriberDTO corpSubscriberDTO,String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            xdmDAO.updateSubsProfileLastUpdateTime(corpSubscriberDTO, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public static KnXDMTalkGroupInfoDTO populateTalkGroup(KnCorpAddlTGInfoDTO atgList) {
        KnXDMTalkGroupInfoDTO xdmTalkGroupInfoDTO = new KnXDMTalkGroupInfoDTO();
        xdmTalkGroupInfoDTO.setGroupId(atgList.getGroupId());
        xdmTalkGroupInfoDTO.setPriority(atgList.getPriority());
        return xdmTalkGroupInfoDTO;
    }
    
    public void updateSubscriberMCSIds(KnSubsProfileDTO subscProfile, String xdmsHome, KnPersisterTxn
            persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.updateSubscriberMCSIds(subscProfile, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> getProfileMdnByBaseMdn(String baseMdn,String xdmsHome,KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getProfileMdnByBaseMdn()";
        knLogger.debug(methodName, "ENTRY baseMdn: ",KnGDPRTemplate.mdn(baseMdn));
        List<String> profileMdn=null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            profileMdn = xdmDAO.getProfileMdnByBaseMdn(baseMdn,persisterTxn);
            knLogger.debug(methodName, " profileMdn- ", KnGDPRTemplate.mdnList(profileMdn));
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return profileMdn;
    }
    
    public void deleteFromMcpttPermInfoProfileMdns(Map<String, Collection<String>> authTargetMap, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "deleteFromMcpttPermInfoProfileMdns(Map<String, Collection<String>>,String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY authTargetMap: ",authTargetMap == null ? authTargetMap : KnGDPRTemplate.mapKeyMdn(authTargetMap));
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
       try {
           corpXdmDao.deleteFromMcpttPerm(authTargetMap, persisterTxn);
       } catch (KnDAOException e) {
           throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
       }
   }
    
    public List<String> getProfileMdnByBaseMdns(List<String> baseMdns,String xdmsHome,KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getProfileMdnByBaseMdns(List<String>,String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY baseMdn: ",baseMdns == null ? baseMdns : KnGDPRTemplate.mdnList(baseMdns));
        List<String> profileMdn=null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            profileMdn = xdmDAO.getProfileMdnByBaseMdns(baseMdns,persisterTxn);
            knLogger.debug(methodName, " profileMdn- ", KnGDPRTemplate.mdnList(profileMdn));
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return profileMdn;
    }

    public Map<String, List<String>> getMapOfProfileMdnByBaseMdn(List<String> baseMdnList,String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        return getMapOfProfileMdnByBaseMdn(baseMdnList, xdmsHome, false, persisterTxn);
    }

    /**
     * This method gets base and profile mdn map.
     * @param baseMdnList
     * @param xdmsHome
     * @param persisterTxn
     * @return Map<String, List<String>>
     * @throws KnCorpBOException
     */
    public Map<String, List<String>> getMapOfProfileMdnByBaseMdn(List<String> baseMdnList,String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getMapOfProfileMdnByBaseMdn(baseMdnList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Set<String> getBaseMdnByProfileMdns(List<String> profileMdns, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getBaseMdnByProfileMdns(profileMdns, xdmsHome, false, persisterTxn);
    }
    public Set<String> getBaseMdnByProfileMdns(List<String> profileMdns, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        Set<String> profileMdn = new HashSet<>();
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            Map<String, Integer> subsContactCountMap = new HashMap<>();
            var mdnListArray = new ArrayList<>(profileMdns);
            var mdnSplitList = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
            for (var mdnBatchList : mdnSplitList) {
                var profileMdnResponse = xdmDAO.getBaseMdnByProfileMdns(mdnBatchList, readOnly, persisterTxn);
                if (null != profileMdnResponse && !profileMdnResponse.isEmpty()) {
                    profileMdn.addAll(profileMdnResponse);
                }
            }
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return profileMdn;
    }

    public Map<String, String> getProfileMdnBaseMdnMap(List<String> profileMdns, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        Map<String, String> baseProfileMdnMap = null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            baseProfileMdnMap = xdmDAO.getProfileMdnBaseMdnMap(profileMdns, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return baseProfileMdnMap;
    }

    public void updateProfileMdnDetails(KnCorpSubscriberDTO subsProfilePersistDTO,Map<String, String> mdnActivsFsMap, String xdmsHome,
                                        Map<String, KnCorpSubscriberDTO> mdnUpmFsMap,
                                        KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.updateProfileMdnDetails(subsProfilePersistDTO,mdnActivsFsMap, mdnUpmFsMap, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnCorpSubscriberDTO> getProfileMdnAndUpmfsByBaseMdn(String baseMdn,String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getProfileMdnAndUpmfsByBaseMdn(baseMdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public static Map<String,String> getMdnMcpttIdMap(List<String> mdnList, KnPersisterTxn
            persisterTxn, String xdmsHome) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getMdnMcpttIdMap(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Set<String> getMDNListByFanIds(Set<Integer> fanIds, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getMDNListByFanIds()";
        try {
            ICorpXdmDAO custCorpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return custCorpXdmDao.getMDNListByFanIds(fanIds, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Integer> getPrivacyOptStatus(Collection<String> mdnList,String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getPrivacyOptStatus(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertIntoMcpttPermInfoForProfileMdns(Collection<KnMcpttPermissionDTO> mcpttMappingDto,List<String> profileMdnsList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.insertIntoMcpttPermInfoForProfileMdns(mcpttMappingDto,profileMdnsList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateToMcpttPermInfoForProfileMdns(Collection<KnMcpttPermissionDTO> mcpttMappingDto,List<String> profileMdnsList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.updateToMcpttPermInfoForProfileMdns(mcpttMappingDto,profileMdnsList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteFromMcpttPermInfoForProfileMdns(List<String> authMdns, Collection<String> targetMdn, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.deleteFromMcpttPermInfoForProfileMdns(authMdns,targetMdn, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Integer> getCommonContactInfoFromMcpttPerm(String authMdn,
                                                      String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getCommonContactInfoFromMcpttPerm(authMdn, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateAuthorizationImpactedTablesForProfileMdns(String pttServerId, List<String> authMdnList, List<String> targetMdnList,
                                                                        int corpId, KnPersisterTxn persisterTxn,
                                                                        Map<String, KnOPDirChgDTO> etagMap) throws KnCorpBOException {
        final String methodName = "updateAuthorizationImpactedTablesForProfileMdns(String, String, int, Collection<String>, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            Map<String, KnOPDirChgDTO> notificationMap = updateAuthImpactedTable(pttServerId, authMdnList, targetMdnList,
                    corpId, persisterTxn, etagMap);
            KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
            notificationMap = commonInfoUtil.setXapRootUri(notificationMap, pttServerId, persisterTxn);
            return notificationMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
                    " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateAuthorizationImpactedTablesForProfileMdnsForUpm(String pttServerId, List<String> authMdnList, List<String> targetMdnList,
                                                                                      int corpId, KnPersisterTxn persisterTxn,
                                                                                      Map<String, KnOPDirChgDTO> etagMap) throws KnCorpBOException {
        final String methodName = "updateAuthorizationImpactedTablesForProfileMdns(String, String, int, Collection<String>, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            Map<String, KnOPDirChgDTO> notificationMap = updateAuthImpactedTableForUpm(pttServerId, authMdnList, targetMdnList,
                    corpId, persisterTxn, etagMap);
            KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
            notificationMap = commonInfoUtil.setXapRootUri(notificationMap, pttServerId, persisterTxn);
            return notificationMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
                    " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateEtagAuthTable(String pttServerId, List<String> authMdnList, List<String> targetMdnList,
                                    int corpId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateEtagAuthTable(String, String, int, Collection<String>, KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            updateAuthTable(pttServerId, authMdnList, targetMdnList,
                    corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
                    " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, List<KnMcpttPermissionDTO>> getTargetMdnListPermissions(List<String> authMdnList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getTargetMdnListPermissions(authMdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> getProfileMdns(List<String> mdnList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getProfileMdns(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public boolean isCommonContactList(Collection<Integer> sublistIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        boolean isCommonContactList = Boolean.FALSE;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            isCommonContactList = xdmDAO.isCommonContactList(sublistIds, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return isCommonContactList;
    }

    public Map<String, Map<String, Integer>> getMdnsFanBanInfo(Collection<String> mdns, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getMDNListByFanIds()";
        try {
            ICorpXdmDAO custCorpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return custCorpXdmDao.getMdnsFanBanInfo(mdns, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Returns a map of MDN → HIERARCHY_ID for each subscriber in the given list.
     * Used for hierarchy-based member filtering in getGroupDetails.
     */
    public Map<String, String> getMdnsHierarchyInfo(Collection<String> mdns, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getMdnsHierarchyInfo()";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            Map<String, KnCorpSubscriberDTO> subsDetails = corpXdmDao.getSubscriberDetails(new ArrayList<>(mdns), readOnly, persisterTxn);
            Map<String, String> mdnHierarchyMap = new HashMap<>();
            for (Map.Entry<String, KnCorpSubscriberDTO> entry : subsDetails.entrySet()) {
                String hierarchyId = entry.getValue().getHierarchyId();
                mdnHierarchyMap.put(entry.getKey(), hierarchyId != null ? hierarchyId.trim() : "");
            }
            knLogger.debug(methodName, "mdnHierarchyMap size - ", mdnHierarchyMap.size());
            return mdnHierarchyMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<Integer> getDistinctFanInfo(Collection<String> mdns, String xdmsHomePttId, KnPersisterTxn persisterTxn, boolean readOnly) throws KnCorpBOException {
        final String methodName = "getDistinctFanInfo(Collection<String>, KnPersisterTxn, Boolean)";
        try {
            ICorpXdmDAO custCorpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return custCorpXdmDao.getDistinctFanInfo(mdns, persisterTxn, readOnly);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<Integer> getDistinctBanInfo(Collection<String> mdns, String xdmsHomePttId, KnPersisterTxn persisterTxn, boolean readOnly) throws KnCorpBOException {
        final String methodName = "getDistinctBanInfo(Collection<String>, KnPersisterTxn, Boolean)";
        try {
            ICorpXdmDAO custCorpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return custCorpXdmDao.getDistinctBanInfo(mdns, persisterTxn, readOnly);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void prepareProfileMDNCorpAdminFs(Map<String, KnCorpSubscriberDTO> mdnUpmFsMap, KnSubscrFeatureInfoDTO reqCorpAdminFeature) {
        for (Map.Entry<String, KnCorpSubscriberDTO> entry : mdnUpmFsMap.entrySet()) {
            KnCorpSubscriberDTO subsInfo = entry.getValue();
            if (subsInfo != null && subsInfo.getCorpAdminFS2() != null) {
                KnFeatureSetUtil featureSetUtil = KnFeatureSetUtil.getInstance();
                BitSet corpAdminBitSet = featureSetUtil.convertHexStringToBitSet(subsInfo.getCorpAdminFS2());
                if (reqCorpAdminFeature.getUserEnableCorpBit() != null) {
                    corpAdminBitSet.set(KnConstants.FEATURE_SET.USERENABLEDISABLE.value(), reqCorpAdminFeature.getUserEnableCorpBit() == 1);
                }
                if (reqCorpAdminFeature.getMcDevice() != null) {
                    corpAdminBitSet.set(KnConstants.FEATURE_SET.MCDEVICE.value(), reqCorpAdminFeature.getMcDevice() == 1);
                }
                if (reqCorpAdminFeature.getWdsPatching() != null) {
                    corpAdminBitSet.set(KnConstants.FEATURE_SET.WDSPATCHING.value(), reqCorpAdminFeature.getWdsPatching() == 1);
                }
                if (reqCorpAdminFeature.getWdsRecording() != null) {
                    corpAdminBitSet.set(KnConstants.FEATURE_SET.WDSRECORDING.value(), reqCorpAdminFeature.getWdsRecording() == 1);
                }
                if (reqCorpAdminFeature.getPttRecording() != null) {
                    corpAdminBitSet.set(KnConstants.FEATURE_SET.PTT_RECORDING_FLAG.value(), reqCorpAdminFeature.getPttRecording() == 1);
                }
                if (reqCorpAdminFeature.getDataRecording() != null) {
                    corpAdminBitSet.set(KnConstants.FEATURE_SET.DATA_RECORDING_FLAG.value(), reqCorpAdminFeature.getDataRecording() == 1);
                }
                if (reqCorpAdminFeature.getVideoRecording() != null) {
                    corpAdminBitSet.set(KnConstants.FEATURE_SET.VIDEO_RECORDING_FLAG.value(), reqCorpAdminFeature.getVideoRecording() == 1);
                }
                String corpAdminFSReq = featureSetUtil.convertBitSetToHexString(corpAdminBitSet);
                subsInfo.setCorpAdminFS2(corpAdminFSReq);
            }
        }
    }

    public Map<String, Integer> getPaginatedAuthUserList(List<String> targetMdnList,int startIndex, int endIndex,
                                                      String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getPaginatedAuthUserList(targetMdnList,startIndex,endIndex, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String,Integer> getPaginatedDestinationOwnerMdns(List<String> destinationMdnList, int startIndex, int endIndex, boolean readOnly, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            return corpXdmDao.getPaginatedDestinationOwnerMdns(destinationMdnList,startIndex,endIndex,readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<String> getMcidsByMdnList(Collection<String> mdns, String xdmsHomePttId, KnPersisterTxn persisterTxn, boolean readOnly) throws KnCorpBOException {
        final String methodName = "getMcidsByMdnList(Collection<String>, String, KnPersisterTxn, Boolean)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getMcidsByMdnList(mdns, persisterTxn, readOnly);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, String> getFanDetailsByCorporateId(int corpId, String xdmsHomePttId, KnPersisterTxn persisterTxn, boolean readyOnly) throws KnCorpBOException {
        final String methodName = "getFanDetailsByCorporateId(int, String, KnPersisterTxn, boolean)";
        try {
            ICorpXdmDAO custCorpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return custCorpXdmDao.getFanDetailsByCorporateId(corpId, persisterTxn, readyOnly);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, String> getBanDetailsByCorporateId(int corpId, String xdmsHomePttId, KnPersisterTxn persisterTxn, boolean readyOnly) throws KnCorpBOException {
        final String methodName = "getBanDetailsByCorporateId(int, String, KnPersisterTxn, boolean)";
        try {
            ICorpXdmDAO custCorpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return custCorpXdmDao.getBanDetailsByCorporateId(corpId, persisterTxn, readyOnly);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

}
