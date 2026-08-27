/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * ************************************************************************
 * <p/>
 * File name:  KnCorpContactInfoUtil.java
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
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.common.resources.KnGeneralUtil;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubscriberDTO;
import com.kodiak.xdms.server.corpmgmt.business.KnCorpBOException;
import com.kodiak.xdms.server.corpmgmt.business.validator.KnCorpBOValidationException;
import com.kodiak.xdms.server.corpmgmt.dao.persister.ICorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dao.persister.db.KnCorpXdmDAO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpActivationDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpInfoDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSublistSubscDistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPCorpSubscContactListDTO;
import com.kodiak.xdms.server.corpmgmt.dto.common.*;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpContactListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpSubscContactListRespDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnContactDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpBCGrpPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnCorpMdnListPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnMdnDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.dto.persistdat.KnSublistDetailsPersistDTO;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants;
import com.kodiak.xdms.server.corpmgmt.resources.KnConstants.SUBSCR_CLIENT_TYPE;
import com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.kodiak.common.resources.KnConstants.IDLIST;
import static com.kodiak.common.resources.KnConstants.IDTYPE;
import static com.kodiak.common.resources.KnConstants.DISABLED;
import static com.kodiak.common.resources.KnConstants.ENABLED;
import static com.kodiak.common.resources.KnConstants.CLIENT_TYPE_WCSR;
import static com.kodiak.xdms.server.common.resources.KnConstants.SERVICE_AUTH_STATUS.MARKED_FOR_ASYNC_DELETION;

import static com.kodiak.common.resources.KnGeneralUtil.calculateActiveFeatureSetBasedOnPv;
import static com.kodiak.xdms.server.common.resources.KnConstants.*;
import static com.kodiak.xdms.server.corpmgmt.resources.KnCorpUtil.isObjectNull;
import static com.kodiak.xdms.server.corpmgmt.resources.KnErrorCodes.Validator.NON_FIRSTNET_FAN;

public class KnCorpContactInfoUtil {
    private static final KnLogger knLogger = KnLogger.getLogger(KnCorpContactInfoUtil.class);
    public static final String CLASS = KnCorpContactInfoUtil.class.getName();
    private KnCorpSublistInfoUtil sublistInfoUtil;
    private KnCorpCommonInfoUtil commonInfoUtil;
    private KnCorpSubsProvInfoUtil corpSubsProvInfoUtil;
    private KnCorpGroupInfoUtil corpGroupInfoUtil;
    //    private KnCorpContactInfoUtil corpContactInfoUtil;// = new KnCorpContactInfoUtil();

    public KnCorpContactInfoUtil() {
        commonInfoUtil = new KnCorpCommonInfoUtil();
        sublistInfoUtil = new KnCorpSublistInfoUtil();
        corpSubsProvInfoUtil =  new KnCorpSubsProvInfoUtil();
        corpGroupInfoUtil = new KnCorpGroupInfoUtil();
    }

    public KnMdnDetailsPersistDTO getPoCSubscriberInfo(KnCorpMdnListPersistDTO contactListDTO,
                                                       String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        return getPoCSubscriberInfo(contactListDTO, pttServerId, FALSE, persisterTxn);
    }

    /**
     * Method to retrieve Subscriber info. This is read only method.
     */
    //for retrieving minimal info for the list of PoC Subscribers, Subscriber contacts, External Contacts
    public KnMdnDetailsPersistDTO getPoCSubscriberInfo(KnCorpMdnListPersistDTO contactListDTO,
                                                       String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getPoCSubscriberInfo(KnCorpMdnListPersistDTO, String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY - readOnly :", readOnly);
        try {
            Collection<String> addedMdnList = contactListDTO.getAddedMdnList();
            List<String> nniSubscrList = new ArrayList<>();
            Collection<String> validPoCSubs = new ArrayList<String>();
            List<String> groupMdnLst = new ArrayList<>();
            List<String> tpMdnLst = new ArrayList<>();
            Collection<KnCorpSubscriberDTO> addedExtlContactList = new ArrayList<KnCorpSubscriberDTO>();
            KnMdnDetailsPersistDTO persistDTO = new KnMdnDetailsPersistDTO();
            Collection<String> removedMdnList = contactListDTO.getRemovedMdnList();
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            if (removedMdnList != null && !removedMdnList.isEmpty()) {
                List<String> grpMdns = corpXdmDao.getGroupMdnInList(removedMdnList, contactListDTO.getCorpId(),readOnly, persisterTxn);
                if (grpMdns != null && grpMdns.size() > 0) {
                    persistDTO.setGrpMdnPresent(Boolean.TRUE);
                    groupMdnLst.addAll(grpMdns);
                }
            }
            if (!isObjectNull(addedMdnList) && !addedMdnList.isEmpty()) {
                Collection<KnCorpSubscriberDTO> addedContactList =
                        corpXdmDao.getPoCSubscribersInfo(contactListDTO, persisterTxn);
                knLogger.debug(methodName, "addedContactList - ", addedContactList);
                persistDTO.setAddedMdnDTO(addedContactList);
                for (KnCorpSubscriberDTO subscriber : addedContactList) {
                    knLogger.debug(methodName, "subscriber - ",subscriber);
                    knLogger.debug(methodName, "subscriber.getMdn() - ", KnGDPRTemplate.mdn(subscriber.getMdn()));
                    knLogger.debug(methodName, "subscriber.getClienttypoe - ", subscriber.getClientType());
                    String mdn = subscriber.getMdn();
                    if (subscriber.isExternalContact()) {
                        addedExtlContactList.add(subscriber);
                        if (KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_SUBSCRIBER != subscriber.getContact_type()) {
                            validPoCSubs.add(mdn);
                        } else {
                            nniSubscrList.add(mdn);
                        }
                    } else {
                        validPoCSubs.add(mdn);
                        int clientType = subscriber.getClientType();
                        if (subscriber.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value()
                                || subscriber.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()) {
                            persistDTO.setGrpMdnPresent(Boolean.TRUE);
                            groupMdnLst.add(mdn);
                            persistDTO.setOnlyAddedGroupMember(true);
                        }

                        if (clientType == SUBSCR_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value()
                        		|| clientType == SUBSCR_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
                        		|| clientType == SUBSCR_CLIENT_TYPE.MOBILEAPI.value()) {
                        	tpMdnLst.add(mdn);
                        }
                    }
                    subscriber.setUa(KnGeneralUtil.getUA(subscriber.getUserAgent(), subscriber.getClientPVmajorVer()));
                }
            }
            contactListDTO.setAddedExternalMdnList(addedExtlContactList);
            persistDTO.setMdnList(validPoCSubs);
            knLogger.debug(methodName, "addedExtlContactList - ", addedExtlContactList);
            persistDTO.setExternalMdnList(addedExtlContactList);
            knLogger.debug(methodName, "nniSubscrList - ", KnGDPRTemplate.mdnList(nniSubscrList));

            persistDTO.setNniSubscrList(nniSubscrList);
            persistDTO.setGroupMdnList(groupMdnLst);
            persistDTO.setTpMdnList(tpMdnLst);
            knLogger.debug(methodName, "EXIT : subscribers Info returned - ", persistDTO);
            return persistDTO;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscribers Info - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnCorpSubscriberDTO> getPoCSubscriberCorporateDetails(Collection<KnCorpSubscriberDTO> subscribersList,
                                                                             String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getPoCSubscriberCorporateDetails(Collection<KnCorpSubscriberDTO>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        Collection<String> mdnList = new ArrayList<String>();
        for (KnCorpSubscriberDTO subsDTO : subscribersList) {
            mdnList.add(subsDTO.getMdn());
        }
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        Map<String, KnCorpSubscriberDTO> subscDBDetails = null;
        try {
            subscDBDetails = corpXdmDao.getSubsribersCorporateDetails(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscribers corporate Details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT : Subscriber corporate details returned - ",KnGDPRTemplate.mapKeyMdn(subscDBDetails));
        return subscDBDetails;
    }


	public Map<String, KnCorpSubscriberDTO> getPoCSubscriberCorporateDetails(List<String> subscribersList, String pttServerId, KnPersisterTxn persisterTxn)
			throws KnCorpBOException {
		final String methodName = "getPoCSubscriberCorporateDetails(Set<String>, String, KnPersisterTxn)";
		knLogger.debug(methodName, "ENTRY :");
		ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
		Map<String, KnCorpSubscriberDTO> subscDBDetails = null;
		try {
			subscDBDetails = corpXdmDao.getSubsribersCorporateDetails(subscribersList, persisterTxn);
		} catch (KnDAOException e) {
			knLogger.error(methodName, "KnDAOException occured while retrieving subscribers corporate Details - ", e);
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
		knLogger.debug(methodName, "EXIT : Subscriber corporate details returned - ", KnGDPRTemplate.mapKeyMdn(subscDBDetails));
		return subscDBDetails;
	}

    public KnMdnDetailsPersistDTO getPoCSubscriberDetails(List<String> subscribersList, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        return getPoCSubscriberDetails(subscribersList, pttServerId, FALSE, persisterTxn);
    }

    /**
     * Method to retrieve subscribers details for requested mdnList. This is read only method.
     */
    public KnMdnDetailsPersistDTO getPoCSubscriberDetails(List<String> subscribersList, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getPoCSubscriberDetails(List<String>, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        KnMdnDetailsPersistDTO subscDBDetails = null;
        try {
            subscDBDetails = corpXdmDao.getPoCSubscriberDetails(subscribersList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscribers corporate Details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT : Subscriber corporate details returned - ", subscDBDetails);
        return subscDBDetails;
    }

    /**
     * To get the subscribers contact list based on the mdns passed in request for delete.
     *
     * @return
     */
    public KnMdnDetailsPersistDTO getSubscriberPrivateContactListInfo(KnCorpMdnListPersistDTO contactListDTO,
                                                                      int privateListId, String pttServerId,
                                                                      KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscriberContactListInfo(KnCorpMdnListPersistDTO, int ,String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            Collection<String> removedMdnList = contactListDTO.getRemovedMdnList();
            Collection<String> validSubsContacts = new ArrayList<String>();
            if (privateListId > 0 && !isObjectNull(removedMdnList) && !removedMdnList.isEmpty()) {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
                Collection<KnCorpSubscriberDTO> subsList = corpXdmDao.getSubscriberPrivateContactListInfo(contactListDTO, privateListId, persisterTxn);

                Collection<String> externalMdnList = new ArrayList<String>();
                for (KnCorpSubscriberDTO subscriber : subsList) {
                    if (subscriber.isExternalContact()) {
                        externalMdnList.add(subscriber.getMdn());
                    } //else {
                    validSubsContacts.add(subscriber.getMdn());
                    // }
                }
                contactListDTO.setRemovedExternalMdnList(externalMdnList);
            }
            KnMdnDetailsPersistDTO persistDTO = new KnMdnDetailsPersistDTO();
            persistDTO.setMdnList(validSubsContacts);
            knLogger.debug(methodName, "EXIT :");
            return persistDTO;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscribers corporate Details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    //todo change this signature (retunrn type) to collection
    public KnMdnDetailsPersistDTO getExternalConatctsInfo(int corpId, Collection<KnCorpSubscriberDTO> contactListDTO,
                                                          String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getExternalConatctsInfo(int, Collection<KnCorpSubscriberDTO> , String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            KnMdnDetailsPersistDTO persistDTO = new KnMdnDetailsPersistDTO();
            if (null != contactListDTO && !contactListDTO.isEmpty()) {
                var mdnListArray = new ArrayList<>(contactListDTO);
                var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
                for(var subList : subsLists){
                    var response = corpXdmDao.getExternalPoCSubscriberInfo(subList, corpId, persisterTxn);
                    if(null != response && !response.isEmpty()){
                        persistDTO.setExternalMdnList(response);
                    }else{
                        persistDTO.setExternalMdnList(new ArrayList<>());
                        knLogger.debug(methodName, "No external contacts found for the given mdn list");
                    }
                }
            }
            return persistDTO;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving external contact details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public void modifySubscriberContactDetails(KnContactDetailsPersistDTO contactDetailsPersistDTO,
                                               int privateSublistId, String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {

        final String methodName = "modifySubscriberContactDetails(KnContactDetailsPersistDTO, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnCorpSubscriberDTO subcriberDTO = contactDetailsPersistDTO.getSubscDto();
        //Added contacts to the private List
        addPrivateContactList(contactDetailsPersistDTO.getNewlyAddedPrivateMembers(), privateSublistId, pttServerId, persisterTxn);
        //Remove contacts from private list
        deleteSubscPrivateContactList(contactDetailsPersistDTO.getContactMdnList(), privateSublistId, pttServerId, persisterTxn);
        //Add sublists to the subscribers
        KnIPCorpSubscContactListDTO inputDTO = (KnIPCorpSubscContactListDTO) contactDetailsPersistDTO.getInputDTO();
        pushSublistToSubscriber(inputDTO.getAddedSublistIds(), subcriberDTO, pttServerId, persisterTxn);
        //Delete sublist from the subscribers
        removeSublistMappingForSubscriber(contactDetailsPersistDTO.getSubscSublistIds(), subcriberDTO, pttServerId
                , persisterTxn);
        knLogger.debug(methodName, "EXIT");
    }

    public void addPrivateContactList(Collection<KnCorpSubscriberDTO> privateContactList, int privateSublistId
            , String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "addPrivateContactList(Collection<KnCorpSubscriberDTO>,int, String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            if (!isObjectNull(privateContactList) && !privateContactList.isEmpty()) {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
                corpXdmDao.addPrivateContactList(privateContactList, privateSublistId, persisterTxn);
            }
            knLogger.debug(methodName, "EXIT");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while adding external contact - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void addPrivateContactList(Collection<KnCorpSubscriberDTO> privateContactList, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "addPrivateContactList(Collection<KnCorpSubscriberDTO>,int, String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            if (!isObjectNull(privateContactList) && !privateContactList.isEmpty()) {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
                corpXdmDao.addPrivateContactList(privateContactList, persisterTxn);
            }
            knLogger.debug(methodName, "EXIT");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while adding external contact - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteSubscPrivateContactList(Collection<String> removePrivateContactList, int privateListId
            , String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "deleteSubscPrivateContactList(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            if (removePrivateContactList != null && !removePrivateContactList.isEmpty()) {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
                corpXdmDao.deleteSubscPrivateContactList(removePrivateContactList, privateListId, persisterTxn);
            }
            knLogger.debug(methodName, "EXIT");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting external contact details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void pushSublistToSubscriber(Collection<Integer> addedSubListId, KnCorpSubscriberDTO subsDTO
            , String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "pushSublistToSubscriber(Collection<Integer>, KnCorpSubscriberDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            if (!isObjectNull(addedSubListId) && !addedSubListId.isEmpty()) {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
                corpXdmDao.pushSublistToSubscriber(addedSubListId, subsDTO, persisterTxn);
                knLogger.debug(methodName, "EXIT");
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while pushing the sublist to subscriber - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void removeSublistMappingForSubscriber(Collection<Integer> removeSubListId, KnCorpSubscriberDTO subsDTO
            , String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "removeSublistMappingForSubscriber(Collection<Integer>, KnCorpSubscriberDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            if (!isObjectNull(removeSubListId) && !removeSubListId.isEmpty()) {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
                corpXdmDao.removeSublistMappingForSubscriber(removeSubListId, subsDTO, persisterTxn);
                knLogger.debug(methodName, "EXIT");
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured removing sublist mapping for subscriber - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateSubscrinberCorpListId(String subscriberMdn, KnCorpSublistDTO corpSublist, String pttServerId,
                                            KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateSubscrinberCorpListId(String,KnCorpSublistDTO, String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.updateSubscCorpListId(subscriberMdn, corpSublist.getSublistId(), persisterTxn);
            knLogger.debug(methodName, "EXIT");
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers corpList id- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateSubcribersImpactedTables(String pttServerId, Collection<String> mdnList,
                                                                     KnPersisterTxn persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnCorpBOException {
        final String methodName = "updateSubcribersImpactedTables(String,Collection<String>,KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            Map<String, KnOPDirChgDTO> notificationMap = new HashMap<String, KnOPDirChgDTO>();
            //added null checks to avoid unwanted calls
            if (mdnList != null && !mdnList.isEmpty()) {
                notificationMap = corpXdmDao.updateSubcribersImpactedTables(mdnList, persisterTxn, etagMap);
                KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
                notificationMap = commonInfoUtil.setXapRootUri(notificationMap, pttServerId, persisterTxn);
            }
            return notificationMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
                    " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateSubcribersImpactedTablesForUpm(String pttServerId, Collection<String> mdnList,
                                                                     KnPersisterTxn persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnCorpBOException {
        final String methodName = "updateSubcribersImpactedTables(String,Collection<String>,KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            Map<String, KnOPDirChgDTO> notificationMap = new HashMap<String, KnOPDirChgDTO>();
            //added null checks to avoid unwanted calls
            if (mdnList != null && !mdnList.isEmpty()) {
                notificationMap = corpXdmDao.updateSubcribersImpactedTablesForUpm(mdnList, persisterTxn, etagMap);
                KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
                notificationMap = commonInfoUtil.setXapRootUri(notificationMap, pttServerId, persisterTxn);
            }
            return notificationMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
                    " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateSubcribersImpactedTables(String pttServerId, Collection<String> mdnList, Collection<KnCorpSubscriberDTO> mdnListDTO, KnPersisterTxn persisterTxn,
                                                                     Map<String, KnOPDirChgDTO> etagMap) throws KnCorpBOException {
        final String methodName = "updateSubcribersImpactedTables(String,Collection<String>,Collection<KnCorpSubscriberDTO>,KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            Map<String, KnOPDirChgDTO> notificationMap = new HashMap<String, KnOPDirChgDTO>();
            // added null checks to avoid unwanted calls
            if (mdnList != null && !mdnList.isEmpty()) {
                notificationMap = corpXdmDao.updateSubcribersImpactedTables(mdnList, mdnListDTO, persisterTxn, etagMap);
                KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
                notificationMap = commonInfoUtil.setXapRootUri(notificationMap, pttServerId, persisterTxn);
            }
            return notificationMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables", " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<String> getCorporateSpecificPoCSubscribersFrmList(Collection<String> mdnList,
                                                                        int corpId, String pttServerId,
                                                                        KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getCorporateSpecificPoCSubscribersFrmList(Collection<String>, int, String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getCorporateSpecificPoCSubscribersFrmList(mdnList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the subscribers list from DB for a given mdn list- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, String> getAllSubscriberContactCount(Collection<String> mdnList,
                                                            String pttServerId, boolean readOnly, KnPersisterTxn
                                                                    persisterTxn) throws KnCorpBOException {
        final String methodName = "getAllSubscriberContactCount(Collection<String>, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getAllSubscriberContactCount(mdnList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the subscribers contact count details- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void pushSublistListToSubscriberList(KnIPCorpSublistSubscDistDTO distDTO, String pttServerId,
                                                KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "pushSublistListToSubscriberList(KnIPCorpSublistSubscDistDTO,String,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.pushSublistListToSubscriberList(distDTO, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while pushing sublist to the subscribers - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * @param corpId        corporate id
     * @param xdmsHomePttId
     * @param persisterTxn
     * @return List of Internal/External/NNI Subscriber
     * @throws KnCorpBOException
     */
    public KnCorpContactListRespDTO getCorpMasterList(int corpId, String xdmsHomePttId, boolean readOnly,
                                                      KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getCorpMasterList(int, String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : corpId=" + corpId);
        try {
            KnCorpContactListRespDTO internalContactsresDTO = getInternalSubscriberList(corpId, 0, null, null, null, xdmsHomePttId, readOnly, persisterTxn);
            KnCorpContactListRespDTO externalContactsResDTO = getExternalSubscriberList(corpId, 0, null, null, null, xdmsHomePttId, readOnly, persisterTxn);
            List<KnExtSubsDetailsDTO> externalContactList = externalContactsResDTO.getExtSubsList();
            KnCorpContactListRespDTO nniContactsResDTO = getNniSubscriberList(corpId, 0, null, null, null, xdmsHomePttId, readOnly, persisterTxn);
            List<KnExtSubsDetailsDTO> nniContactList = nniContactsResDTO.getExtSubsList();
            externalContactList.addAll(nniContactList);
            internalContactsresDTO.setExtSubsList(externalContactList);
            internalContactsresDTO.setThirdPartySubsc(internalContactsresDTO.getThirdPartySubsc());
            knLogger.debug(methodName, "After DB call to get Contact Details of the corporate members and the external/nni contact for the corporate.");
            knLogger.debug(methodName, "Exit Point.Response returned - ", internalContactsresDTO);
            return internalContactsresDTO;
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving master list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnIPCorpActivationDTO> getActivationCodeForCorpoateSubscriber(Collection<String> mdnList, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getActivationCodeForCorpoateSubscriber(KnIPCorpContactListDTO, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getActivationCodeForCorpoateSubscriber(mdnList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscriber private list members - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnCorpSubscriberDTO> getOnlyBaseMdn(List<String> mdnList, String pttServerId, int corpId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getOnlyBaseMdn(List<String>, String, int, boolean, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubscriberDto(mdnList, corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving base subscriber ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns the corporate resource list for the subscribers.
     *
     * @param contactDTO
     * @param maxContacts
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public KnCorpSubscContactListRespDTO getCorpResourceList(KnIPCorpContactDTO contactDTO, int maxContacts, String
            pttServerId, KnPersisterTxn persisterTxn, boolean isProfilemdn, int clientPvMajorVersion) throws KnCorpBOException {
        final String methodName = "getCorpResourceList(KnIPCorpContactDTO,  int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        KnCorpSubscContactListRespDTO respDTO;
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            List<String> subscContactList = corpXdmDao.getCorpResourceList(contactDTO, maxContacts, pttServerId, true, persisterTxn);
            knLogger.debug(methodName, "After DB call to get the subscribers contact list - ", KnGDPRTemplate.mdnList(subscContactList));
            String requestingMdn = contactDTO.getMdn();

            //If Requesting mdn is profile mdn then fetching base mdn of the respective profile mdn
            if(isProfilemdn){
                List<String> profileMdns = new ArrayList<>();
                profileMdns.add(contactDTO.getMdn());
                Map<String, String> baseMdnInfo = corpXdmDao.getProfileMdnBaseMdnMap(profileMdns, true, persisterTxn);
                requestingMdn = baseMdnInfo.get(contactDTO.getMdn());
            }
            knLogger.debug(methodName, "After DB call requestingMdn - ", KnGDPRTemplate.mdn(requestingMdn));
            Set<String> nonCommonContactList = corpXdmDao.getCorpNonCommonContactList(contactDTO.getMdn(), maxContacts, pttServerId, true, persisterTxn);
            knLogger.debug(methodName, "nonCommonContactList - ", KnGDPRTemplate.mdnList(nonCommonContactList));
            List<String> commonContactList = corpXdmDao.getCorpCommonContactList(requestingMdn, maxContacts, pttServerId, true, persisterTxn);
            if(null != commonContactList && !commonContactList.isEmpty()){
                for(String nonCommonContact : nonCommonContactList){
                    commonContactList.remove(nonCommonContact);
                }
            }
            if(isProfilemdn && commonContactList != null && !commonContactList.isEmpty() && contactDTO.getClientType() == com.kodiak.common.resources.KnConstants.CLIENT_TYPE_XCAP){
                subscContactList.addAll(commonContactList);
                knLogger.debug(methodName, "Adding Base mdn's common contact list to profile mdns - ", KnGDPRTemplate.mdnList(subscContactList));
            }
            knLogger.debug(methodName, "After Filtering commonContactList - ", KnGDPRTemplate.mdnList(commonContactList));
            respDTO = new KnCorpSubscContactListRespDTO();
            if (!subscContactList.isEmpty()) {
                Map<String, KnCorpSubscriberDTO> contactListMap = corpXdmDao.getSubscriberDto(subscContactList, contactDTO.getCorpId(), true, persisterTxn);
                int corpId = contactDTO.getCorpId();
                List<String> extMembersList = filterInternalExternalContactsForSubc(contactListMap, subscContactList, corpId);
                knLogger.debug(methodName, "EXIT extMembersList ", KnGDPRTemplate.mdnList(extMembersList));
                Collection<KnCorpSubscriberDTO> externalContactsDetails = corpXdmDao.getExternalPoCSubscriberInfo(extMembersList,
                        corpId, true, persisterTxn);
                if(extMembersList != null){
                    extMembersList.forEach(contactListMap::remove);
                }
                List<String> extMdnList = new ArrayList<>();
                List<String> extSubsMpttMdnList = new ArrayList<>();
                for(KnCorpSubscriberDTO subsc : externalContactsDetails){
                    String mdn = subsc.getMdn();
                    KnCorpSubscriberDTO subsDTO = contactListMap.get(mdn);
                    if(subsDTO != null ){
                        subsc.setClientType(subsDTO.getClientType());
                        subsc.setUserAgent(subsDTO.getUserAgent());
                        subsc.setClientPVmajorVer(subsDTO.getClientPVmajorVer());
                        String activeFsBasedOnPv = calculateActiveFeatureSetBasedOnPv(subsDTO.getSubsActiveFS2(), clientPvMajorVersion);
                        subsc.setSubsActiveFS2(activeFsBasedOnPv);
                        if (KnConstants.PROTOCOL_VERSION_22.equals(contactDTO.getProtocolVersion())) {
                            subsc.setCameraType(subsDTO.getCameraType());
                        }
                    }
                    if (subsc.getContact_type() == KnConstants.CONTACT_TYPE_EXTERNAL_CONTACT) {
                        subsc.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_CONTACT);
                        extMdnList.add(mdn);
                    } else {
                        subsc.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_SUBSCRIBER);
                        extSubsMpttMdnList.add(mdn);
                    }
                }
                knLogger.debug(methodName, "extMdnList: ", KnGDPRTemplate.mdnList(extMdnList), "extSubsMpttMdnList: ", KnGDPRTemplate.mdnList(extSubsMpttMdnList));
                //Setting the AliasId for External contact and ExternalSubscriber as contacts.
                Map<String, KnCorpSubsEntitiesDTO> subsEntitiesDetails = getSubsEntitiesDetails(extMdnList, pttServerId,true, persisterTxn);
                knLogger.debug(methodName, "subsEntitiesDetails: ", KnGDPRTemplate.mapKeyMdn(subsEntitiesDetails));
                for(KnCorpSubscriberDTO subsc : externalContactsDetails){
                    if(subsEntitiesDetails.containsKey(subsc.getMdn())){
                        subsc.setAliasMdn(subsEntitiesDetails.get(subsc.getMdn()).getAliasMdn());
                        subsc.setUserId(subsEntitiesDetails.get(subsc.getMdn()).getUserId());
                        String activeFsBasedOnPv = calculateActiveFeatureSetBasedOnPv(subsEntitiesDetails.get(subsc.getMdn()).getActiveFs2(), clientPvMajorVersion);
                        subsc.setSubsActiveFS2(activeFsBasedOnPv);
                        subsc.setCameraType(subsEntitiesDetails.get(subsc.getMdn()).getCameraType());
                    }
                }
                Map<String, KnMcpttPermissionDTO> targetMdnMap = corpXdmDao.getAuthUserPermissions(contactDTO.getMdn(), true, persisterTxn);
                Map<String, Integer> isAuthMap = commonInfoUtil.isAuthUserMap(targetMdnMap);
                CopyOnWriteArrayList<KnCorpSubscriberDTO> subscFinalContactList = new CopyOnWriteArrayList<>();
                subscFinalContactList.addAll(contactListMap.values());
                knLogger.debug(methodName, "EXIT contactListMap.values() ", contactListMap.values());
                subscFinalContactList.addAll(externalContactsDetails);
                knLogger.debug(methodName, "EXIT externalContactsDetails ", externalContactsDetails);
                if (!subscFinalContactList.isEmpty()) {
                    subscFinalContactList.forEach(contact -> {
						if (contact.getUserAgent() != null) {
							contact.setUa(KnGeneralUtil.getUA(contact.getUserAgent(), contact.getClientPVmajorVer()));
						}
                        if(isAuthMap.containsKey(contact.getMdn())){
                            contact.setIsAuthUser(isAuthMap.get(contact.getMdn()));
                        }
                        if(commonContactList.contains(contact.getMdn())){
                            contact.setCommonContact(1);
                        }
                        if (clientPvMajorVersion < PROTOCOL_VERSION_16_X) {
                            contact.setSubsActiveFS2(KnGeneralUtil.convertActiveFs2toHexActiveFs1(contact.getSubsActiveFS2()));
                        } else {
                            String activeFsBasedOnPv = calculateActiveFeatureSetBasedOnPv(contact.getSubsActiveFS2(), clientPvMajorVersion);
                            contact.setSubsActiveFS2(activeFsBasedOnPv);
                        }
                        //filtering async deletion in progress mdns
                        if (CLIENT_TYPE_WCSR == contactDTO.getClientType() && contact.getServiceAuthStatus() == MARKED_FOR_ASYNC_DELETION.value()){
                            subscFinalContactList.remove(contact);
                        }

                    });
                }
                respDTO.setContactList(subscFinalContactList);
            }
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return respDTO;
    }

    /**
     * This method receives internal members and all members and filter out only external members and returns the same.
     *
     * @param internalMemList
     * @param subscContactList
     * @return
     */
    public List<String> filterInternalExternalContactsForSubc(Set<String> internalMemList, List<String> subscContactList) {
        List<String> extContList = new ArrayList<String>(subscContactList);
        extContList.removeAll(internalMemList);
        return extContList;
    }

    public KnCorpSubscContactListRespDTO getCorpSubscContactList(KnIPCorpContactDTO contactDTO, int corpListId, int
            maxContactsPerSubsc, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "getCorpSubscContactList(KnIPCorpContactDTO, int, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        KnCorpSubscContactListRespDTO respDTO = new KnCorpSubscContactListRespDTO();
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            knLogger.debug(methodName, "Before DB call to get Sublist present for the subscriber - ", KnGDPRTemplate.mdn(contactDTO.getMdn()));
            Collection<KnCorpSublistDTO> sublistList =
                    getSubscMappedSublistListWithMemberCount(contactDTO, corpListId, pttServerId, true, persisterTxn);
            knLogger.debug(methodName, "After DB call to get Sublist present for the subscriber - ", sublistList);
            Map<String, Collection<KnCorpSubscriberDTO>> subsPrivateContactMap =
                    getSubscPrivateInternalExternalContacts(contactDTO, corpListId, maxContactsPerSubsc,
                            pttServerId, true, persisterTxn);
            knLogger.debug(methodName, "After DB call to get private contacts for the subscribers - ",
                   KnGDPRTemplate.mapKeyMdn(subsPrivateContactMap));
            if (!sublistList.isEmpty() || !subsPrivateContactMap.isEmpty()) {
                knLogger.debug(methodName, "Setting the response DTO.");
                respDTO.setSublistList(sublistList);
                List<KnCorpSubscriberDTO> subscContactList = new ArrayList<KnCorpSubscriberDTO>();

                Collection<KnCorpSubscriberDTO> internalContacts = subsPrivateContactMap.get(KnConstants.INTERNAL);
                if (internalContacts != null && !internalContacts.isEmpty()) {
                    subscContactList.addAll(internalContacts);
                }
                knLogger.debug(methodName, "Before DB call to get external contacts details.");

                Collection<KnCorpSubscriberDTO> externalContacts = subsPrivateContactMap.get(KnConstants.EXTERNAL);
                if (externalContacts != null && !externalContacts.isEmpty()) {
                    List<String> extMdnList = new ArrayList<>(externalContacts.size());
                    for (KnCorpSubscriberDTO subscriberDTO : externalContacts) {
                        extMdnList.add(subscriberDTO.getMdn());
                    }
                    Collection<KnCorpSubscriberDTO> extContactList =
                            corpXdmDao.getExternalPoCSubscriberInfo(extMdnList, contactDTO.getCorpId(), true, persisterTxn);
                    knLogger.debug(methodName, "After DB call to get external contacts details - ", extContactList);
                    List<String> extIntMember = new ArrayList<>();
                    List<String> extMember = new ArrayList<>();
                    for(KnCorpSubscriberDTO corpSubscriberDTO : extContactList){
                        if(corpSubscriberDTO.getContact_type() == KnConstants.CONTACT_TYPE_EXTERNAL_CONTACT){
                            extIntMember.add(corpSubscriberDTO.getMdn());
                        }else{
                            extMember.add(corpSubscriberDTO.getMdn());
                        }
                    }

                    knLogger.debug(methodName, "extIntMember: ", KnGDPRTemplate.mdnList(extIntMember), "extMember: ", KnGDPRTemplate.mdnList(extMember));
                    //Setting the External contact and ExternalSubscriber as contacts.
                    Map<String, KnCorpSubsEntitiesDTO> subsEntitiesDetails = getSubsEntitiesDetails(extIntMember, pttServerId, true, persisterTxn);
                    knLogger.debug(methodName, "subsEntitiesDetails: ", subsEntitiesDetails);
                    for(KnCorpSubscriberDTO corpSubscriberDTO : extContactList){
                        if(subsEntitiesDetails.containsKey(corpSubscriberDTO.getMdn())){
                            corpSubscriberDTO.setAliasMdn(subsEntitiesDetails.get(corpSubscriberDTO.getMdn()).getAliasMdn());
                            corpSubscriberDTO.setUserId(subsEntitiesDetails.get(corpSubscriberDTO.getMdn()).getUserId());
                            corpSubscriberDTO.setSubsActiveFS2(subsEntitiesDetails.get(corpSubscriberDTO.getMdn()).getActiveFs2());
                            }
                    }
                    knLogger.debug(methodName, "After DB call to get external contacts details - ", extContactList);
                    subscContactList.addAll(extContactList);
                }
                respDTO.setContactList(subscContactList);
            }
            return respDTO;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscriber contact list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpSublistDTO> getSubscMappedSublistList(KnIPCorpContactDTO contactDTO, int corpListId,
                                                                  String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubsMappedSublistList(KnIPCorpContactDTO, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubsMappedSublistList(contactDTO, corpListId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscriber mapped sublist list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }

    public Collection<KnCorpSublistDTO> getSubscMappedSublistListWithMemberCount(KnIPCorpContactDTO contactDTO, int corpListId,
                                                                                 String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubsMappedSublistList(KnIPCorpContactDTO, int, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubscMappedSublistListWithMemberCount(contactDTO, corpListId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscriber mapped sublist list - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }

    public Map<String, Collection<KnCorpSubscriberDTO>> getSubscPrivateInternalExternalContacts(
            KnIPCorpContactDTO contactDTO, int corpListId,
            int maxContactsPerSubsc,
            String pttServerId, boolean readOnly,
            KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscPrivateInternalExternalContacts(KnIPCorpContactDTO, int, int, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubscPrivateInternalExternalContacts(contactDTO, corpListId, maxContactsPerSubsc, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscriber private list members - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public KnCorpSubscContactListRespDTO addExternalContacts(KnIPCorpContactListDTO contactListDTO,
                                                             String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        final String methodName = "addExternalContacts(KnIPCorpContactListDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.addExternalContacts(contactListDTO, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscriber private list members - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }

    public KnCorpResponseDTO modifyExternalContact(KnIPCorpContactDTO contactDTO, String pttServerId,
                                                   KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "modifyExternalContact(KnIPCorpContactDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.modifyExternalContacts(contactDTO, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while modifying the external contact details- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<String> getSubscMdnsHavingExtContact(KnIPCorpContactDTO contactDTO, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubscMdnsHavingExtContact(KnIPCorpContactDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubscMdnsHavingExtContact(contactDTO, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fecthing subscribers having the external contact a members- ",
                    e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpSubscriberDTO> getSubscPrivateMemberListDetails(int privateContactListId, String pttServerId,
                                                                            KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubscPrivateMemberListDetails(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubscPrivateMemberListDetails(privateContactListId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching subscribers privare members details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<String> getContactMDNs(String mdn, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getContactMDNs(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getContactMDNs(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the contact MDNs ",
                    "from DB for a given mdn - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpSubscriberDTO> getDistinctMembers(Collection<KnCorpSubscriberDTO>
                                                                      addedMemList, Collection<Integer> addedSublistIds, int corpId, String pttServerId, KnPersisterTxn
                                                                      persisterTxn) throws KnCorpBOException {

        final String methodName = "getDistinctMembers(Collection<KnCorpSubscriberDTO>, Collection<Integer>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        Collection<KnCorpSubscriberDTO> distinctMemberList = new ArrayList<KnCorpSubscriberDTO>();
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            if (addedSublistIds != null && !addedSublistIds.isEmpty()) {
                distinctMemberList = corpXdmDao.selectDistinctMembers(addedMemList, addedSublistIds, corpId, persisterTxn);
                knLogger.debug(methodName, "Distinct Sublist members - ", distinctMemberList);
            } else {
                knLogger.debug(methodName, "No Sublist. Only Memberlst");
                if (addedMemList != null && !addedMemList.isEmpty()) {
                    distinctMemberList.addAll(addedMemList);
                }
            }

            return distinctMemberList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving Sublist members - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpSubscriberDTO> getDistinctMembersForSublist(Collection<String> addedMdnList,
                                                                        Collection<Integer> addedSublistIds,
                                                                        String pttServerId,
                                                                        KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "getDistinctMembersForSublist(Collection<String>, Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getDistinctMembersForSublist(addedMdnList, addedSublistIds, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getDistinctMembersForSublist - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnCorpContactListRespDTO getExternalContactDetails(Collection<KnIPCorpContactDTO> contactDTO, int
            corpId, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "getExternalContactDetails(Collection<KnIPCorpContactDTO>, int, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            KnCorpContactListRespDTO respDTO = corpXdmDao.getExternalContactDetails(contactDTO, corpId, readOnly, persisterTxn);
            int contactCount = respDTO.getContactList().size() + respDTO.getExtSubsMap().size();
            int memberCountPassed = contactDTO.size();
            if (contactCount != memberCountPassed) {
                knLogger.error(methodName, "External Contact Does not exist for corporation - ");
                throw new KnCorpBOException(KnErrorCodes.BOEntity.ETXRENAL_CONTACT_NOT_FOUND_FOR_CORPORATE,
                        "External Contact Does not exist for corporation.");
            }
            return respDTO;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getExternalContactDetails - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public int getSubscribersDocumentEtag(String mdn, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {

        final String methodName = "getSubscribersDocumentEtag(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubscribersDocumentEtag(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getSubscribersDocumentEtag - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String,Integer> getSubscribersDocumentEtag(Collection<String> mdnList, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {

        final String methodName = "getSubscribersDocumentEtag(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubscribersDocumentEtag(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getSubscribersDocumentEtag - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public int getSubscribersCount(String subscriberMdn, int corpId, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        return getSubscribersCount(subscriberMdn, corpId, false, pttServerId, persisterTxn);
    }

    public int getSubscribersCount(String subscriberMdn, int corpId, boolean readOnly, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {

        final String methodName = "getSubscribersCount(String, int,boolean, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY - readOnly :", readOnly);
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubscribersCount(subscriberMdn, corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getSubscribersCount - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public long getCorporateEtag(int corpId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getCorporateEtag(corpId, pttServerId, false, persisterTxn);
    }

    public long getCorporateEtag(int corpId, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "getCorporateEtag(int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getCorporateEtag(corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getCorporateEtag - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method queries the DG.XDM_CORPRESOURCELISTINDEXDOC table for subscriber etag.
     * 7     *
     *
     * @param mdn
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public int getSubscriberResourceListEtag(String mdn, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubscriberResourceListEtag(mdn, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public long updateCorporateEtag(int corpId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "updateCorporateEtag(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.updateCorporateEtag(corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateCorporateEtag - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Integer> getFinalMemberContactCount(Collection<Integer> finalSublistListInDB, Collection<String>
            finalMdnInPrivateList, String subscriberMdn, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "getFinalMemberContactCount(Collection<Integer>, Collection<String>, String" +
                "String, KnPersisterTxn";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getFinalMemberContactCount(finalSublistListInDB, finalMdnInPrivateList, subscriberMdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getFinalMemberContactCount - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateSubscribersContactCount(Collection<String> mdnList, int maxSubsContactCount, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "updateSubscribersContactCount(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.updateSubscribersContactCount(mdnList, maxSubsContactCount, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateSubscribersContactCount - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateSubcribersResourceListIndexDoc(Collection<String> mdnList, String xdmsHomePttId,
                                                                           Map<String, KnOPDirChgDTO> etagMaps, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateSubcribersResourceListIndexDoc(Collection<String>, String, Map<String, KnOPDirChgDTO>, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.updateSubcribersResourceListIndexDoc(mdnList, etagMaps, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateSubcribersResourceListIndexDoc - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }

    public void updateEtagForSubMdn(Map<String,Integer> mdnEtagMap, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateSubcribersResourceListIndexDoc(Collection<String>, String, Map<String, KnOPDirChgDTO>, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            corpXdmDao.updateEtagForSubMdn(mdnEtagMap, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateSubcribersResourceListIndexDoc - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }


    public Map<String, KnOPDirChgDTO> updateDistinctSubcribersDirectory(Collection<String> mdnList,
                                                                        Collection<Integer> groupIdLst,
                                                                        Map<String, KnOPDirChgDTO> eTags, String xdmsHomePttId,
                                                                        KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateDistinctSubcribersDirectory(Collection<String>,  Collection<Integer>, Map<String, KnOPDirChgDTO>, String, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            Map<String, KnOPDirChgDTO> notificationMap = eTags;
            //added null checks to avoid unwanted calls
            if (mdnList != null && !mdnList.isEmpty() || groupIdLst != null && !groupIdLst.isEmpty()) {
                notificationMap = corpXdmDao.updateDistinctSubcribersDirectory(mdnList, groupIdLst,
                        eTags, persisterTxn);
                KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
                notificationMap = commonInfoUtil.setXapRootUri(notificationMap, xdmsHomePttId, persisterTxn);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateSubcribersDirectory - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } catch (Throwable e) {
            knLogger.error(methodName, "Throwable occured while updateSubcribersDirectory - ", e);
        }
        return eTags;
    }

    public Map<String, KnOPDirChgDTO> updateDistinctSubcribersDirectory(Collection<String> mdnList,
                                                                        Collection<Integer> groupIdLst,
                                                                        Map<String, KnOPDirChgDTO> eTags, String xdmsHomePttId,
                                                                        boolean upmDocNotify,KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateSubcribersDirectory(Collection<String>,  Collection<Integer>, Map<String, KnOPDirChgDTO>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            Map<String, KnOPDirChgDTO> notificationMap = eTags;
            //added null checks to avoid unwanted calls
            if (mdnList != null && !mdnList.isEmpty() || groupIdLst != null && !groupIdLst.isEmpty()) {
                notificationMap = corpXdmDao.updateDistinctSubcribersDirectory(mdnList, groupIdLst,
                        eTags,upmDocNotify, persisterTxn);
                KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
                notificationMap = commonInfoUtil.setXapRootUri(notificationMap, xdmsHomePttId, persisterTxn);
            }
            return eTags;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateSubcribersDirectory - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateDistinctSubcribersDirectoryClone(Collection<String> mdnList,
                                                                        Collection<Integer> groupIdLst,
                                                                        Map<String, KnOPDirChgDTO> eTags, String xdmsHomePttId,
                                                                        boolean upmDocNotify,KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateDistinctSubcribersDirectoryClone(Collection<String>,  Collection<Integer>, Map<String, KnOPDirChgDTO>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            Map<String, KnOPDirChgDTO> notificationMap = eTags;
            //added null checks to avoid unwanted calls
            if (mdnList != null && !mdnList.isEmpty() || groupIdLst != null && !groupIdLst.isEmpty()) {
                notificationMap = corpXdmDao.updateDistinctSubcribersDirectoryClone(mdnList, groupIdLst,
                        eTags,upmDocNotify, persisterTxn);
                KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
                notificationMap = commonInfoUtil.setXapRootUri(notificationMap, xdmsHomePttId, persisterTxn);
            }
            return eTags;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateSubcribersDirectory - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateDistinctSubcribersETags(Collection<String> mdnList,
                                                                    Collection<String> selfMdnList,
                                                                    Collection<Integer> groupIdLst,
                                                                    Map<String, KnOPDirChgDTO> eTags,
                                                                    boolean updateResourceDaoETag,
                                                                    String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateDistinctSubcribersETags(Collection<String>, Collection<String>  Collection<Integer>, " +
                "Map<String, KnOPDirChgDTO>, String, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            Map<String, KnOPDirChgDTO> notificationMap = eTags;
            if (mdnList != null && !mdnList.isEmpty() || groupIdLst != null && !groupIdLst.isEmpty()) {
                notificationMap = corpXdmDao.updateDistinctSubcribersETags(mdnList, selfMdnList, groupIdLst,
                        eTags, updateResourceDaoETag, persisterTxn);
                KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
                notificationMap = commonInfoUtil.setXapRootUri(notificationMap, xdmsHomePttId, persisterTxn);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateDistinctSubcribersETags - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } catch (Throwable e) {
            knLogger.error(methodName, "Throwable occured while updateDistinctSubcribersETags - ", e);
        }
        return eTags;
    }

	public Map<String, KnOPDirChgDTO> setPocHomeForProfileMdns(Collection<String> mdnList,
			Collection<Integer> groupIdLst, Map<String, KnOPDirChgDTO> eTags, String xdmsHomePttId,
			KnPersisterTxn persisterTxn) throws KnCorpBOException {
		final String methodName = "setPocHomeForProfileMdns(Collection<String>,  Collection<Integer>, Map<String, KnOPDirChgDTO>, String, KnPersisterTxn)";
		knLogger.debug(methodName, "ENTRY :");
		try {
			ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
			Map<String, KnOPDirChgDTO> notificationMap = eTags;
			//added null checks to avoid unwanted calls
			if (mdnList != null && !mdnList.isEmpty() || groupIdLst != null && !groupIdLst.isEmpty()) {
				notificationMap = corpXdmDao.setPocHomeForProfileMdns(mdnList, groupIdLst, eTags, persisterTxn);
				KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
				notificationMap = commonInfoUtil.setXapRootUri(notificationMap, xdmsHomePttId, persisterTxn);
			}
			return eTags;
		} catch (KnDAOException e) {
			knLogger.error(methodName, "KnDAOException occured while updateSubcribersDirectory - ", e);
			throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
		}
	}

    public Collection<Integer> filterFinalSublistToBeAddedInDb(Collection<Integer> finalSublistListInDB,
                                                               KnSublistDetailsPersistDTO dbSublistList) {
        final String methodName = "filterFinalSublistToBeAddedInDb(Collection<Integer>, KnSublistDetailsPersistDTO";
        knLogger.debug(methodName, "ENTRY :");
        Collection<Integer> finalSublistIds = new ArrayList<Integer>();
        for (Integer sublistId : finalSublistListInDB) {
            if (!dbSublistList.getSublistIds().contains(sublistId)) {
                finalSublistIds.add(sublistId);
            }
        }
        return finalSublistIds;
    }

    public Collection<KnCorpSubscriberDTO> getExternalPoCSubscriberDetails(Collection<KnCorpSubscriberDTO> externalMember,
                                                                           int intCorpId, String xdmsHome,
                                                                           KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getExternalPoCSubscriberDetails(Collection<KnCorpSubscriberDTO>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            return corpXdmDao.getExternalPoCSubscriberDetails(externalMember, intCorpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getExternalPoCSubscriberDetails - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public Map<String, KnCorpSubscriberDTO> getSubsribersCorporateDetails(Collection<String> mdnList, int corpId,
                                                                          String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubsribersCorporateDetails(Collection<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        Map<String, KnCorpSubscriberDTO> subscDBDetails = null;
        try {
            subscDBDetails = corpXdmDao.getSubsribersCorporateDetails(mdnList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscribers corporate Details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT :");
        return subscDBDetails;
    }

    public int getExternalContactCount(int corpId, int contactType, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getExternalContactCount(int, int, String, KnPersisterTxn)";

        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getExternalContactCount(corpId, contactType, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscribers corporate Details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteExtMember(Collection<String> mdnList, int corpId, String xdmsHomePttId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "deleteExtMember(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            corpXdmDao.deleteExtMember(mdnList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting external contacts - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateSubscribersDirectory(Collection<String> mdnList,
                                                                 Map<String, KnOPDirChgDTO> etagMap, String xdmsHomePttId,
                                                                 KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateSubscribersDirectory(Collection<String>,  Map<String, KnOPDirChgDTO>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            etagMap = corpXdmDao.updateSubscribersDirectroy(mdnList, etagMap, persisterTxn);
            KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
            etagMap = commonInfoUtil.setXapRootUri(etagMap, xdmsHomePttId, persisterTxn);
            return etagMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting external contacts - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnMdnDetailsPersistDTO getPoCSubscriberInfoDetails(KnCorpMdnListPersistDTO corpMdnListDTO,
                                                              String xdmsHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getPoCSubscriberInfoDetails(KnCorpMdnListPersistDTO,  String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            KnMdnDetailsPersistDTO mdnDetails = getPoCSubscriberInfo(corpMdnListDTO, xdmsHome, persisterTxn);
            Collection<KnCorpSubscriberDTO> externalMembers = mdnDetails.getExternalMdnList();
            if (externalMembers != null && !externalMembers.isEmpty()) {
                KnMdnDetailsPersistDTO mdnDetailsPersistDTO = getExternalConatctsInfo(corpMdnListDTO.getCorpId(),
                        externalMembers, xdmsHome, persisterTxn);
                mdnDetails.setExternalMdnList(mdnDetailsPersistDTO.getExternalMdnList());
                mdnDetails.setMdnList(null);
            }
            return mdnDetails;
        } finally {
            knLogger.debug(methodName, "EXIT");

        }
    }

    public Map<Integer, Collection<KnCorpSubscriberDTO>> getSublistListDistributionList
            (Collection<Integer> sublistList, String xdmsHomePttId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSublistListDistributionList(Collection<Integer>,  String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            Map<Integer, Collection<KnCorpSubscriberDTO>> distInfo = new HashMap<Integer, Collection<KnCorpSubscriberDTO>>();
            if (sublistList != null && !sublistList.isEmpty()) {
                distInfo = corpXdmDao.getSublistListDistributionList(sublistList, persisterTxn);
            }
            return distInfo;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting the sublist distribution List - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } finally {
            knLogger.debug(methodName, "EXIT");

        }

    }

    public void deleteCorporateExternalMembers(KnIPCorpInfoDTO corpInfoDTO, String xdmHome, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "deleteCorporateExternalMembers(KnIPCorpInfoDTO,  String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHome);
        try {
            corpXdmDao.deleteCorporateExternalMembers(corpInfoDTO, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting the coporates external members - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } finally {
            knLogger.debug(methodName, "EXIT");

        }
    }

    public int getCorpSubscriberCount(int corpId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getCorpSubscriberCount(int,  String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getCorpSubscriberCount(corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the subscribers count for the corporate- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } finally {
            knLogger.debug(methodName, "EXIT");

        }
    }

    public void nullifyContactCorpIdInExtTable(int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "nullifyContactCorpIdInExtTable(int,  String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            corpXdmDao.nullifyContactCorpIdInExtTable(corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the subscribers count for the corporate- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } finally {
            knLogger.debug(methodName, "EXIT");

        }
    }

    public Collection<String> getFinalMemberGroupContactCount(Collection<Integer> sublistMappedToGroup,
                                                              Collection<KnCorpSubscriberDTO> privateMemberList,
                                                              Collection<KnCorpSubscriberDTO> mdnsToBeAddedToPrivateList,
                                                              KnMdnDetailsPersistDTO contactMdnPersistDto, String xdmsHome,
                                                              KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getFinalMemberGroupContactCount(Collection<Integer>,  Collection<KnCorpSubscriberDTO>," +
                "Collection<KnCorpSubscriberDTO>,KnMdnDetailsPersistDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getFinalMemberGroupContactCount(sublistMappedToGroup, privateMemberList, mdnsToBeAddedToPrivateList,
                    contactMdnPersistDto, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while fetching the actual group contact count- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        } finally {
            knLogger.debug(methodName, "EXIT");

        }
    }

    public boolean checkExternalContactExists(KnIPCorpContactDTO contactDTO, int
            corpId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "checkExternalContactExists(KnIPCorpContactDTO, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        boolean subscriberExists = false;
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            Collection<KnIPCorpContactDTO> subscList = new ArrayList<KnIPCorpContactDTO>();
            subscList.add(contactDTO);
            KnCorpContactListRespDTO respDTO = corpXdmDao.getExternalContactDetails(subscList, corpId, false, persisterTxn);
            int contactCount = respDTO.getContactList().size();
            if (contactCount > 0) {
                subscriberExists = true;
            }
            return subscriberExists;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while checkExternalContactExists - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    public void insertMembersIntoCorpContactList(Map<String, Collection<KnCorpSubscriberDTO>> mdnContactListMap,
                                                 String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "insertMembersIntoCorpContactList(Map<String, Collection<KnCorpSubscriberDTO>>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            //added null checks to avoid unwanted calls
            if (mdnContactListMap != null && !mdnContactListMap.isEmpty()) {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
                corpXdmDao.insertMembersIntoCorpContactList(mdnContactListMap, persisterTxn);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while insertMembersIntoCorpContactList - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Method deletes the members from the contact list of a members
     * Linked list to maintain the order of the responmse from the DB to decide if member is deleted or not
     *
     * @param mdnContactListMap
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public LinkedHashMap<String, LinkedList<Integer>> deleteMembersFromCorpContactList(LinkedHashMap<String, LinkedList<String>> mdnContactListMap,
                                                                                       String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "deleteMembersFromCorpContactList(Map<String, Collection<String>>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY Point : ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            //added null checks to avoid unwanted calls
            if (mdnContactListMap != null && !mdnContactListMap.isEmpty()) {
                return corpXdmDao.deleteMembersFromCorpContactList(mdnContactListMap, persisterTxn);
            } else {
                return null;
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleteMembersFromCorpContactList - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Collection<String>> getSubscribersContactList(Collection<String> mdnList, String pttServerId,
                                                                     KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "getSubscribersContactList(Collection<String>, String, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            Map<String, Collection<String>> subscriberContactList = new HashMap<String, Collection<String>>();
            if (mdnList != null && !mdnList.isEmpty()) {
                subscriberContactList = corpXdmDao.getSubscribersContactList(mdnList, persisterTxn);
            }
            return subscriberContactList;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getSubscribersContactList - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateOwnerMdnInContactList(KnIPCorpContactDTO contactDTO, String xdmsHomePttId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "updateOwnerMdnInContactList(KnIPCorpContactDTO, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            corpXdmDao.updateOwnerMdnInContactList(contactDTO, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateOwnerMdnInContactList - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteSubscribersContactList(String ownerMdn, String pttServerId,
                                             KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "deleteSubscribersContactList(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.deleteSubscribersContactList(ownerMdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting from SubscribersContactList - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteBulkSubscribersContactList(List<String> mdnList, String pttServerId,
                                             KnPersisterTxn persisterTxn) throws KnCorpBOException, KnDAOException {
        final String methodName = "deleteBulkSubscribersContactList(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        corpXdmDao.deleteBulkSubscribersContactList(mdnList, persisterTxn);
    }

    public Map<String, KnCorpSubscriberDTO> getPoCSubscriberExistMap(Collection<String> mdnList,
                                                                     String xdmsHomePttId,
                                                                     KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getPoCSubscriberExistList(Collection<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getPoCSubscriberExistMap(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting from SubscribersContactList - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateSusbcribersCorpIdInImpactedTables(String corpId, String mdn, String xdmHomePttId,
                                                        KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateSusbcribersCorpIdInImpactedTables(String, String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHomePttId);
            corpXdmDao.updateSusbcribersCorpIdInImpactedTables(corpId, mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateSusbcribersCorpIdInImpactedTables - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns a Map of MDN and private contact list id of each MDN.
     *
     * @param completeMdnList
     * @param xdmHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<String, Integer> getSubscribersPrivateListId(Collection<String> completeMdnList, String xdmHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscribersPrivateListId(Collection<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHome);
            return corpXdmDao.getSubscribersPrivateListId(completeMdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getSubscribersPrivateListId - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Collection<KnCorpSubscriberDTO>> getSubscribersPrivateList(Map<String, Integer> subscPrivateListMap, String xdmHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscribersPrivateList((Map<String, Integer> , String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHome);
            return corpXdmDao.getSubscribersPrivateList(subscPrivateListMap, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getSubscribersPrivateList - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void insertCorpContactListMembers(Map<String, Collection<KnCorpSubscriberDTO>> memberOfPrivateList,
                                             String xdmHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "insertMembersIntoCorpContactList(Map<String, Collection<KnCorpSubscriberDTO>>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHome);
            corpXdmDao.insertCorpContactListMembers(memberOfPrivateList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while insertMembersIntoCorpContactList - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void deleteSusbcribersFromExtContactTables(String mdn, String xdmHomePttId,
                                                      KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "deleteSusbcribersFromExtContactTables(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHomePttId);
            corpXdmDao.deleteSusbcribersFromExtContactTables(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateSusbcribersCorpIdInImpactedTables - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This methos will return map of corpid and the ext contact name in his corpid of a subacriber
     *
     * @param mdn
     * @param xdmsHomePttId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<Integer, String> getCorpIdListWhereIsExternalContact(String mdn, String xdmsHomePttId,
                                                                    KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getCorpIdListWhereIsExternalContact(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getCorpIdListWhereIsExternalContact(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting external contacts - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, KnCorpSubscriberDTO> getCorpIdContactTypeWhereIsExternalContact(String mdn, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getCorpIdContactTypeWhereIsExternalContact(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getCorpIdContactTypeWhereIsExternalContact(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting external contacts - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void addExternalContactsInAllCorp(Map<Integer, KnCorpSubscriberDTO> corpIdExtContactNameMap, String newMdn, int corpId, String
            xdmHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "addExternalContactsInAllCorp(Map<Integer, String>, String, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHomePttId);
            corpXdmDao.addExternalContactsInAllCorp(corpIdExtContactNameMap, newMdn, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateSusbcribersCorpIdInImpactedTables - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void updateCorporateEtagForIdList(Collection<Integer> corpIdList, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "updateCorporateEtagForIdList(Collection<Integer>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.updateCorporateEtagForIdList(corpIdList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateCorporateEtag - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, Collection<KnCorpSubscriberDTO>> getSubscribersContactDeatilsList(Collection<String> completeMdnList,
                                                                                         String xdmHome,
                                                                                         KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscribersContactDeatilsList(Collection<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHome);
            return corpXdmDao.getSubscribersContactDeatilsList(completeMdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getSubscribersContactDeatilsList - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, String> getSubscribersName(Set<String> mdnList, String xdmsHome,
                                                  KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getSubscribersName(mdnList, xdmsHome, false, persisterTxn);
    }

    /**
     * This method returns a Map of subscribers and subscribers name. This is read only method.
     *
     * @param mdnList
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<String, String> getSubscribersName(Set<String> mdnList, String xdmsHome, boolean readOnly,
                                                  KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscribersName(Set<String>, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        Map<String, String> subscribersNameMap = new HashMap<>();
        if (null == mdnList || mdnList.isEmpty()) {
            knLogger.exit(methodName, "EXIT : mdnList is null or Empty");
            return subscribersNameMap;
        }
        var mdnListArray = new ArrayList<>(mdnList);
        var subsLists = KnGeneralUtil.splitList(mdnListArray, BULK_UPDATE_SIZE);
        for (var subsList : subsLists) {
            var subSubscriberNameMap = getSubSubscriberName(new HashSet<>(subsList), xdmsHome, readOnly, persisterTxn);
            if (null != subSubscriberNameMap && !subSubscriberNameMap.isEmpty()) {
                subscribersNameMap.putAll(subSubscriberNameMap);
            }
        }
        int subsSize = subscribersNameMap == null ? 0 : subscribersNameMap.size();
        knLogger.info(methodName, "EXIT : getSubscribersName size ", subsSize);

        return subscribersNameMap;
    }

    public Map<String, String> getSubSubscriberName(Set<String> mdnList, String xdmsHome, boolean readOnly,
                                                    KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubSubscriberName(Set<String>, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            return corpXdmDao.getSubscribersName(mdnList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, String> getSubscribersName(Collection<KnCorpSubscriberDTO> mdnList, String xdmsHome,
                                                  KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscribersName(Set<String>, String, KnPersisterTxn)";
        Set<String> addedMdnListForName = new HashSet<>();
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            for (KnCorpSubscriberDTO subscriberDTO : mdnList) {
                addedMdnListForName.add(subscriberDTO.getMdn());
            }
            return corpXdmDao.getSubscribersName(addedMdnListForName,FALSE, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    /**
     * This method return the corporates where he is an external subscriber
     *
     * @param mdn
     * @param xdmsHomePttId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Collection<Integer> getExtCorpForSub(String mdn, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getExtCorpForSub(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getExtCorpForSub(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getExtCorpForSub() - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /* public Collection<KnCorpSubscriberDTO> getPoCSubscriberInfo(Collection<String> addedMdnList,
                                                                String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getPoCSubscriberInfo(KnCorpMdnListPersistDTO, String, KnPersisterTxn)";
        knLogger.info( methodName, "ENTRY : ");
        try {
            Collection<KnCorpSubscriberDTO> addedContactList = new ArrayList<KnCorpSubscriberDTO>();
            if (!isObjectNull(addedMdnList) && !addedMdnList.isEmpty()) {
                ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
                addedContactList = corpXdmDao.getPoCSubscribersInfo(addedMdnList, persisterTxn);
            }
            return addedContactList;
        } catch (KnDAOException e) {
            knLogger.error( methodName, "KnDAOException occured while retrieving subscribers Info - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }*/

    /**
     * Method determines the unique members between the added mdn and the added sublsit list
     *
     * @param addedMdnList
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */

    public Collection<KnCorpSubscriberDTO> getSubscribersInfo(Collection<String> addedMdnList,
                                                              String pttServerId,
                                                              KnPersisterTxn persisterTxn) throws KnCorpBOException {

        final String methodName = "getSubscriberInfo(Collection<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        Collection<KnCorpSubscriberDTO> pocSubscrList = null;
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            List<String> nniSubscrList = new ArrayList<>();
            if (addedMdnList != null) {
                nniSubscrList.addAll(addedMdnList);
            }
            pocSubscrList = corpXdmDao.getSubscribersInfo(addedMdnList, persisterTxn);
            for (KnCorpSubscriberDTO subscriberDTO : pocSubscrList) {
                String mdn = subscriberDTO.getMdn();
                nniSubscrList.remove(mdn);
            }
            for (String mdn : nniSubscrList) {
                KnCorpSubscriberDTO subscriberDTO = new KnCorpSubscriberDTO();
                subscriberDTO.setMdn(mdn);
                subscriberDTO.setExternalContact(true);
                subscriberDTO.setContact_type(KnConstants.CONTACT_TYPE_EXTERNAL_SUBSCRIBER);
                pocSubscrList.add(subscriberDTO);
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getSubscriberInfo - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return pocSubscrList;
    }

    public Map<String, KnCorpSubscriberDTO> getSubscIsMemOfDispGrpDetails(Collection<String> mdnList, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscIsMemOfDispGrpDetails(Collection<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
            Map<String, KnCorpSubscriberDTO> subscIsMemOfDispGrpDetails = new HashMap<>();
            var mdnArrayList = new ArrayList<>(mdnList);
            var spllitedMdnList = KnGeneralUtil.splitList(mdnArrayList, BULK_UPDATE_SIZE);
            for (var mdnListBatch : spllitedMdnList) {
                var subscIsMemOfDispGrpDetailsForBatch = corpXdmDao.getSubscIsMemOfDispGrpDetails(mdnListBatch, persisterTxn);
                if (subscIsMemOfDispGrpDetailsForBatch != null && !subscIsMemOfDispGrpDetailsForBatch.isEmpty()) {
                    subscIsMemOfDispGrpDetails.putAll(subscIsMemOfDispGrpDetailsForBatch);
                }
            }
            return subscIsMemOfDispGrpDetails;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getSubscIsMemOfDispGrpDetails() - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<String> getPocSubscribersDetails(Collection<String> contactMDNs, int corpId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getPocSubscribersDetails(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getPocSubscribersDetails(contactMDNs, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getPocSubscribersDetails() - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String,Integer> getMdnCorpIdMapping(Collection<String> contactMDNs ,String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getPocSubscribersDetails(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getMdnCorpIdMapping(contactMDNs, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getPocSubscribersDetails() - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpSubscriberDTO> getSubscriberProfileInfo(Collection<String> mdnList, int corpId,
                                                                    String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubscriberProfileInfo(Collection<KnCorpSubscriberDTO>, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");

        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        Collection<KnCorpSubscriberDTO> subscDBDetails = null;
        try {
            subscDBDetails = corpXdmDao.getSubscriberProfileInfo(mdnList, corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscribers corporate Details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT : Subscriber corporate details returned - ", subscDBDetails);
        return subscDBDetails;
    }

    public Collection<KnCorpSubscriberDTO> getSubscriberProfileDetails(Collection<String> mdnList, int corpId,
                                                                       String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubscriberProfileDetails(Collection<KnCorpSubscriberDTO>, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");

        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        Collection<KnCorpSubscriberDTO> subscDBDetails = null;
        try {
            subscDBDetails = corpXdmDao.getSubscriberProfileDetails(mdnList, corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscribers corporate Details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT : Subscriber corporate details returned - ", subscDBDetails);
        return subscDBDetails;
    }

    /**
     * his method returns a Map of subscribers and subscriber name from dg.pocsubscrinfo table
     *
     * @param mdnList
     * @param corpId
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<String, KnSubscriberDTO> getSubscribersNameForCorp(List<String> mdnList, int corpId, String
            pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return getSubscribersNameForCorp(mdnList, corpId, pttServerId, false, persisterTxn);
    }

    public Map<String, KnSubscriberDTO> getSubscribersNameForCorp(List<String> mdnList, int corpId, String
            pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscribersNameForCorp(List, int, String, boolean, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubscribersNameForCorp(mdnList, corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnSubscriberDTO> getSubscribersNameForCorpReadOnly(List<String> mdnList, int corpId, String
            pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscribersNameForCorpReadOnly(List, int, String, boolean, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubscribersNameForCorp(mdnList, corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpSubscriberDTO> getExternalPoCSubscriberInfo(List<String> extMdnList, int corpId, String
            pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getExternalPoCSubscriberInfo(List, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getExternalPoCSubscriberInfo(extMdnList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns a Map of external contact MDN and name from the dg.extcorpcontact table
     *
     * @param extMdnList
     * @param corpId
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<String, KnSubscriberDTO> getExtContName(List<String> extMdnList, int corpId, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getExtContName(List, int, String, boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getExtContName(extMdnList, corpId,readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnSubscriberDTO> getExtContNameReadonly(List<String> extMdnList, int corpId, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getExtContNameReadonly(List, int, String, boolean, KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getExtContName(extMdnList, corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnCorpSubscriberDTO selectPocSubscriberInfo(String mdn, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return selectPocSubscriberInfo(mdn, pttServerId, false, persisterTxn);
    }

    public KnCorpSubscriberDTO selectPocSubscriberInfo(String mdn, String pttServerId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "selectPocSubscriberInfo(String, String,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.selectPocSubscriberInfo(mdn, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "DAO Exception occured - ", e.getMessage());
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnCorpBOException(KnErrorCodes.BOEntity.INVALID_POC_SUBSCRIBERS,
                        "Profile Information not found", e);
            }
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method query the DG.POCSUBSCRINFO and DG.CORPGROUPDISTINFO tables and return all subscribers in the
     * PAM Account ID without groups.
     *
     * @param pamAccId
     * @param clientType
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public List<String> getCorpSubsWithNoGrps(int pamAccId, int clientType, String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getCorpSubsWithNoGrps(pamAccId, clientType, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method query the DG.POCSUBSCRINFO and DG.CORPLISTDISTINFO tables and return all subscribers in the
     * PAM Account ID who does not have contacts.
     *
     * @param pamAccId
     * @param clientType
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public List<String> getCorpSubsWithNoConts(int pamAccId, int clientType, String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getCorpSubsWithNoConts(pamAccId, clientType, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Method to return a Map of MDN who has deleting MDN as contact with the deleting contact list.
     *
     * @param mdnList
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<String, List<String>> getSubsContactList(Collection<String> mdnList, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubsContactList(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * Method to return a unique mdn list from a Collection of list.
     *
     * @param mdnListColl
     * @return
     */
    public List<String> getUniqueSubs(Collection<List<String>> mdnListColl) {
        Set<String> uniqueList = new HashSet<String>();
        for (List<String> list : mdnListColl) {
            uniqueList.addAll(list);
        }
        return new ArrayList<String>(uniqueList);
    }

    /**
     * This method deleted the contactlist entries for mdnList.
     *
     * @param mdnList
     * @param pttServerId
     * @param persisterTxn
     * @throws KnCorpBOException
     */
    public void deleteCorpContactList(List<String> mdnList, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.deleteCorpContactList(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method deleted excernal contact entries from extcorpcontact table.
     *
     * @param mdnList
     * @param xdmHomePttId
     * @param persisterTxn
     * @throws KnCorpBOException
     */
    public void deleteSusbcribersFromExtContactTables(List<String> mdnList, String xdmHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHomePttId);
            corpXdmDao.deleteSusbcribersFromExtContactTables(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method removes if private contact list id = 0.
     *
     * @param pvtContLstMap
     * @return
     */
    public Map<String, Integer> removeDummyEntry(Map<String, Integer> pvtContLstMap) {
        Map<String, Integer> contListMap = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : pvtContLstMap.entrySet()) {
            int listId = entry.getValue();
            if (listId != 0) {
                contListMap.put(entry.getKey(), listId);
            }
        }
        return contListMap;
    }

    /**
     * This method returns list od corporate Id where menList exist as external contact
     *
     * @param mdnList
     * @param xdmHomePttId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public List<Integer> getCorpIdList(List<String> mdnList, String xdmHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHomePttId);
            return corpXdmDao.getCorpIdList(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }

    public KnCorpSubscriberDTO getSubscriberDetail(String mdn, String pttServerId,
                                                   KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscriberProfileDetail(String, int, KnPersisterTxn)";
        knLogger.debug(methodName);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        KnCorpSubscriberDTO subscDBDetails = null;
        try {
            subscDBDetails = corpXdmDao.getSubscriberDetail(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscribers Detail - ", e);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(e.getErrorCode())) {
                throw new KnCorpBOException(KnErrorCodes.BOEntity.INVALID_POC_SUBSCRIBERS,
                        "Profile Information not found", e);
            }
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT : Subscriber detail returned - ", subscDBDetails);
        return subscDBDetails;
    }

    public boolean isExternalSubscriber(String mdn, int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "isExternalSubscriber(mdn, corpId, KnPersisterTxn)";
        knLogger.debug(methodName);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        boolean isExt = false;
        try {
            isExt = corpXdmDao.isExternalSubscriber(mdn, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while checking isExternalSubscriber ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT : isExternalSubscriber", isExt);
        return isExt;
    }

    public boolean isDispatchMemberPresent(List<String> mappedMdnList, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "isDispatchMemberPresent(List<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        boolean isDispMemExist = false;
        try {
            isDispMemExist = corpXdmDao.isDispatchMemberPresent(mappedMdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscribers Detail - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT : Does a dispatch member exists as  - ", isDispMemExist);
        return isDispMemExist;
    }


    public ArrayList<String> getInternalNonDispatchMember(Collection<String> mdnList, int corpId, String pttServerId,
                                                          KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getInternalNonDispatchMember(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        ArrayList<String> intNonDispMembers = null;
        try {
            intNonDispMembers = corpXdmDao.getInternalNonDispatchMember(mdnList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving internal non dispatcher - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT :   - ", intNonDispMembers);
        return intNonDispMembers;
    }

    public Collection<String> getDispContacts(Collection<String> nonDispGrpMember, int corpId, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getDispContacts(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        ArrayList<String> intNonDispMembers = null;
        try {
            intNonDispMembers = corpXdmDao.getDispContacts(nonDispGrpMember, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving internal non dispatcher - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT :   - ", intNonDispMembers);
        return intNonDispMembers;
    }

    public Map<String, Boolean> getLocationPubFeaturebit(ArrayList<String> pocHome, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getLocationPubFeaturebit(ArrayList<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName);
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        Map<String, Boolean> isLocationPubEnabled;
        try {
            isLocationPubEnabled = corpXdmDao.getLocationPubFeaturebit(pocHome, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving internal non dispatcher - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT :   - ", isLocationPubEnabled);
        return isLocationPubEnabled;
    }

    /**
     * @param corpId       corporate id
     * @param xdmHomePttId
     * @param persisterTxn
     * @return count of internal subscriber
     * @throws KnCorpBOException
     */
    public int getInternalSubscriberCount(int corpId, String xdmHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHomePttId);
            return corpXdmDao.getInternalSubscriberCount(corpId, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }


    /**
     * @param corpId       corporate id
     * @param contactType  contactType = 1 means External Contact and contactType = 2 means NNI Contact
     * @param xdmHomePttId
     * @param persisterTxn
     * @return return the count for External/NNI contact
     * @throws KnCorpBOException
     */
    public int getExternalSubscriberCount(int corpId, int contactType, String xdmHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHomePttId);
            return corpXdmDao.getExternalSubscriberCount(corpId, contactType, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * @param corpId        corporate id
     * @param filterType    possible value 0/1/2/3 0[All] 1[Internal Subscriber] 2[External Subscriber] 3[NNI Subscriber]
     * @param fetchSize     number of Row to fetch
     * @param nextToken     the page number to fetch
     * @param sortType      By [1]MDN OR By [2]NAME, 0 means no sorting
     * @param xdmsHomePttId
     * @param persisterTxn
     * @return DTO having List of Internal Subscriber
     * @throws KnCorpBOException
     */
    public KnCorpContactListRespDTO getInternalSubscriberList(int corpId, Integer filterType,
                                                              Integer fetchSize, Integer nextToken, Integer sortType,
                                                              String xdmsHomePttId, boolean readOnly,
                                                              KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getInternalSubscriberList(int,Integer,Integer, Integer, Integer,String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :", "corpId", corpId, "filterType", filterType, "fetchSize", fetchSize, "nextToken", nextToken, "sortType", sortType, "xdmsHomePttId", xdmsHomePttId);
        try {
            KnCorpContactListRespDTO contactListRespDTO = new KnCorpContactListRespDTO();
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            KnCorpContactBean corpContactBean = corpXdmDao.getInternalSubscriberList(corpId, filterType, fetchSize, nextToken, sortType, readOnly, persisterTxn);
            List<String> mdnList = new ArrayList<>(corpContactBean.getCorpSubscriberDTOMap().keySet());
            Map<String, Map<String, Object>> subscriberPackageMap = corpXdmDao.getInternalSubscriberPackageMap(mdnList, persisterTxn);
            contactListRespDTO.setThirdPartySubsc(corpContactBean.getThirdPartyUsers());
            List<String> provisionedMdnList = corpContactBean.getProvisionedMdnList();
            Map<String, KnCorpSubscriberDTO> corpContacts = corpContactBean.getCorpSubscriberDTOMap();
            Map<String, KnIPCorpActivationDTO> activationCodeMap = corpXdmDao.getActivationCodeForCorpoateSubscriber(provisionedMdnList, readOnly, persisterTxn);
            Map<String, Integer> subsContactCountMap = corpXdmDao.getSubscriberContactCount(corpContacts.keySet(), readOnly, persisterTxn);
            List<KnCorpSubscriberDTO> contactList = new ArrayList<KnCorpSubscriberDTO>();
            for (Map.Entry<String, KnCorpSubscriberDTO> entry : corpContacts.entrySet()) {
                String mdn = entry.getKey();
                KnCorpSubscriberDTO corpSubscriberDTO = entry.getValue();
                Integer count = subsContactCountMap.get(mdn);
                if (count != null) {
                    corpSubscriberDTO.setContactCount(count);
                }
                if (subscriberPackageMap.get(mdn) != null) {
                    corpSubscriberDTO.setPackageInfo(subscriberPackageMap.get(mdn));
                }
                contactList.add(corpSubscriberDTO);
            }
            //TO-DO: This block need to be reviewed as this is not used anywhere
            for (Map.Entry<String, KnIPCorpActivationDTO> entry : activationCodeMap.entrySet()) {
                String mdn = entry.getKey();
                KnIPCorpActivationDTO activationDTO = entry.getValue();
                KnCorpSubscriberDTO subscriber = corpContacts.get(mdn);
                subscriber.setActivationCode(activationDTO.getActivationCode());
                subscriber.setActivationTimestamp(activationDTO.getActivationTimestamp());
                subscriber.setExpiryTime(activationDTO.getExpiryTimestamp());
            }
            knLogger.debug(methodName, "Exit Point, contactList=", contactList.toString());
            contactListRespDTO.setContactList(contactList);
            return contactListRespDTO;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving master list", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * @param corpId        corporate id
     * @param filterType    possible value 4 [Internal Interop Subscriber]
     * @param fetchSize     number of Row to fetch
     * @param nextToken     the page number to fetch
     * @param sortType      By [1]MDN OR By [2]NAME, 0 means no sorting
     * @param xdmsHomePttId
     * @param persisterTxn
     * @return DTO having List of NNI Subscriber
     * @throws KnCorpBOException
     */
    public KnCorpContactListRespDTO getInternalInteropSubscriberList(int corpId, Integer filterType,
                                                                     Integer fetchSize, Integer nextToken,
                                                                     Integer sortType, String xdmsHomePttId, boolean readOnly,
                                                                     KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getInternalInteropSubscriberList(int,Integer,Integer, Integer, Integer,String,boolean,KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :", "corpId", corpId, "filterType", filterType, "fetchSize", fetchSize, "nextToken", nextToken, "sortType", sortType, "xdmsHomePttId", xdmsHomePttId);
        try {
            KnCorpContactListRespDTO contactListRespDTO = new KnCorpContactListRespDTO();
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            KnCorpContactBean corpContactBean = corpXdmDao.getInternalInteropSubscriberList(corpId, filterType, fetchSize, nextToken, sortType, readOnly, persisterTxn);
            List<String> mdnList = new ArrayList<>(corpContactBean.getCorpSubscriberDTOMap().keySet());
            Map<String, Map<String, Object>> subscriberPackageMap = corpXdmDao.getInternalSubscriberPackageMap(mdnList, persisterTxn);
            contactListRespDTO.setThirdPartySubsc(corpContactBean.getThirdPartyUsers());
            Map<String, KnCorpSubscriberDTO> corpContacts = corpContactBean.getCorpSubscriberDTOMap();
            Map<String, Integer> subsContactCountMap = corpXdmDao.getSubscriberContactCount(corpContacts.keySet(), readOnly, persisterTxn);
            List<KnCorpSubscriberDTO> contactList = new ArrayList<>();

            corpContacts.entrySet().stream().filter(obj -> {
                if (null != obj.getValue().getSubscriberFs2()) {
                    boolean interOpFlag = KnGeneralUtil.getFeatureBitValue(obj.getValue().getSubscriberFs2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.INTEROPFEATURE.value());
                    boolean dataInterOp = KnGeneralUtil.getFeatureBitValue(obj.getValue().getSubscriberFs2(), com.kodiak.common.resources.KnConstants.FEATURE_SET.DATA_INTER_OP.value());
                    return (interOpFlag || dataInterOp);
                } else {
                    return false;
                }
            }).forEach(obj -> {
                KnCorpSubscriberDTO corpSubscriberDTO = obj.getValue();
                Integer count = subsContactCountMap.get(obj.getKey());
                if (count != null) {
                    corpSubscriberDTO.setContactCount(count);
                }
                if (subscriberPackageMap.get(obj.getKey()) != null) {
                    corpSubscriberDTO.setPackageInfo(subscriberPackageMap.get(obj.getKey()));
                }
                contactList.add(corpSubscriberDTO);
            });
            knLogger.debug(methodName, "Exit Point, contactList= ", contactList.toString());
            contactListRespDTO.setContactList(contactList);
            return contactListRespDTO;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occurred while retrieving master list", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * @param corpId        corporate id
     * @param filterType    possible value 0/1/2/3 0[All] 1[Internal Subscriber] 2[External Subscriber] 3[NNI Subscriber]
     * @param fetchSize     number of Row to fetch
     * @param nextToken     the page number to fetch
     * @param sortType      By [1]MDN OR By [2]NAME, 0 means no sorting
     * @param xdmsHomePttId
     * @param persisterTxn
     * @return DTO having List of External Subscriber
     * @throws KnCorpBOException
     */
    public KnCorpContactListRespDTO getExternalSubscriberList(int corpId, Integer filterType,
                                                              Integer fetchSize, Integer nextToken, Integer sortType,
                                                              String xdmsHomePttId, boolean readOnly,
                                                              KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            KnCorpContactListRespDTO contactListRespDTO = new KnCorpContactListRespDTO();
            List<KnExtSubsDetailsDTO> extSubsDetailsDTOs = corpXdmDao.getExternalSubscriberList(corpId, filterType,
                    fetchSize, nextToken, sortType, readOnly,
                    persisterTxn);
            contactListRespDTO.setExtSubsList(extSubsDetailsDTOs);
            return contactListRespDTO;
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(),
                    e);
        }
    }

    /**
     * @param corpId        corporate id
     * @param filterType    possible value 0/1/2/3 0[All] 1[Internal Subscriber] 2[External Subscriber] 3[NNI Subscriber]
     * @param fetchSize     number of Row to fetch
     * @param nextToken     the page number to fetch
     * @param sortType      By [1]MDN OR By [2]NAME, 0 means no sorting
     * @param xdmsHomePttId
     * @param persisterTxn
     * @return DTO having List of NNI Subscriber
     * @throws KnCorpBOException
     */
    public KnCorpContactListRespDTO getNniSubscriberList(int corpId, Integer filterType,
                                                         Integer fetchSize, Integer nextToken, Integer sortType, String xdmsHomePttId, boolean readOnly,
                                                         KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            KnCorpContactListRespDTO contactListRespDTO = new KnCorpContactListRespDTO();
            List<KnExtSubsDetailsDTO> extSubsDetailsDTOs = corpXdmDao.getNniSubscriberList(corpId, filterType,
                    fetchSize, nextToken, sortType, readOnly,
                    persisterTxn);
            contactListRespDTO.setExtSubsList(extSubsDetailsDTOs);
            return contactListRespDTO;
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(),
                    e);
        }
    }

    /**
     * This method query DG.ExtSubscrInfo table to return the profileId in KnCorpSubscriberDTO of each mdn list.
     *
     * @param extSubsList
     * @param xdmHomePttId
     * @param persisterTxn
     * @return Map<String,KnCorpSubscriberDTO>
     * @throws KnCorpBOException
     */
    public Map<String, KnCorpSubscriberDTO> getExtSubsMap(List<String> extSubsList, String xdmHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHomePttId);
            return corpXdmDao.getExtSubsMap(extSubsList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method returns the distinct profileId Lists from the input list of DTOs.
     *
     * @param extSubsDtoList
     * @param xdmHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public List<Integer> getExtSubsrProfilelist(Collection<KnCorpSubscriberDTO> extSubsDtoList, String xdmHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        List<Integer> profileIdList;
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHome);
            List<String> mdnList = new ArrayList<>(extSubsDtoList.size());
            boolean pocExtContExist = false;
            for (KnCorpSubscriberDTO dto : extSubsDtoList) {
                if (dto.isExternalContact()) {
                    if (dto.getContact_type() == KnConstants.CONTACT_TYPE_EXTERNAL_SUBSCRIBER) {
                        mdnList.add(dto.getMdn());
                    } else {
                        pocExtContExist = true;
                    }
                }
            }
            profileIdList = corpXdmDao.getExtSubsrProfilelist(mdnList, persisterTxn);
            if (pocExtContExist) {
                profileIdList.add(KnConstants.PROFILE_ID_KODIAK_EXTERNAL_CONTACT);
            }
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return profileIdList;
    }

    public List<KnCorpSubscriberDTO> getSubscPrivateMemberList(int privateContactListId, String pttServerId, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscPrivateMemberList(int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getSubscPrivateMemberList(privateContactListId, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<KnCorpSubscriberDTO> getDistinctMembersList(Collection<KnCorpSubscriberDTO> membersList, List<KnCorpSubscriberDTO>
            distAddedSublistMemLst, int corpId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getDistinctMembersList(Collection<KnCorpSubscriberDTO>, List<distAddedSublistMemLst>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        knLogger.debug(methodName, "membersList :",membersList);
        knLogger.debug(methodName, "distAddedSublistMemLst :",distAddedSublistMemLst);
        List<KnCorpSubscriberDTO> allDistinctMembers = new ArrayList<>();
        if (membersList != null) {
            allDistinctMembers.addAll(membersList);
        }
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            List<String> intnalSublistMembers = new ArrayList<>();
            List<String> externalSublistMembers = new ArrayList<>();
            for (KnCorpSubscriberDTO corpSubscriberDTO : distAddedSublistMemLst) {
                if (corpSubscriberDTO.isExternalContact()) {
                    externalSublistMembers.add(corpSubscriberDTO.getMdn());
                } /*else {
                    intnalSublistMembers.add(corpSubscriberDTO.getMdn());
                }*/
                intnalSublistMembers.add(corpSubscriberDTO.getMdn());
            }
            knLogger.debug(methodName, "externalSublistMembers :", KnGDPRTemplate.mdnList(externalSublistMembers));
            knLogger.debug(methodName, "intnalSublistMembers :", KnGDPRTemplate.mdnList(intnalSublistMembers));
            Collection<KnCorpSubscriberDTO> intSubsDetails = getSubscribersInfo(intnalSublistMembers,  xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "intSubsDetails :", intSubsDetails);
            Map<String,KnSubscriberDTO> extSubsDetailList = corpXdmDao.getExtContName(externalSublistMembers, corpId,false, persisterTxn);
            Collection<KnCorpSubscriberDTO> extSubsDetailDTOs = corpXdmDao.getExternalPoCSubscriberInfo(externalSublistMembers, corpId, persisterTxn);
            knLogger.debug(methodName, "extSubsDetailList :", extSubsDetailList);
            List<KnCorpSubscriberDTO> allSublistMembers = new ArrayList<>(intSubsDetails);
            if (!allSublistMembers.isEmpty()) {
                allSublistMembers.forEach(subscriberDTO -> {
                    knLogger.debug(methodName, "subscriberDTO :", subscriberDTO);
                    if (extSubsDetailList.containsKey(subscriberDTO.getMdn())) {
                        knLogger.debug(methodName, "subscriberDTO mdn:", KnGDPRTemplate.mdn(subscriberDTO.getMdn()));
                        knLogger.debug(methodName, "subscriberDTO Name:", extSubsDetailList.get(subscriberDTO.getMdn()));
                        subscriberDTO.setName(extSubsDetailList.get(subscriberDTO.getMdn()).getNetworkName());
                        knLogger.debug(methodName, "subscriberDTO mdn:", subscriberDTO.getContact_type());
                        if (subscriberDTO.getCorpId() > 0) {
                            knLogger.debug(methodName, "setting the contact type");
                            subscriberDTO.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_CONTACT);
                        } else {
                            subscriberDTO.setContact_type(KnConstants.XCAP_DIFF_CONTACT_TYPE_EXTERNAL_SUBSCRIBER);
                        }
                        knLogger.debug(methodName, "remove the external poc subscriber   :", extSubsDetailDTOs.remove(subscriberDTO));
                    }
                    subscriberDTO.setUa(KnGeneralUtil.getUA(subscriberDTO.getUserAgent(), subscriberDTO.getClientPVmajorVer()));
                    subscriberDTO.setClientPVmajorVer(subscriberDTO.getClientPVmajorVer());
                    if (!allDistinctMembers.contains(subscriberDTO)) {
                        knLogger.debug(methodName, "Adding to the list ");
                        allDistinctMembers.add(subscriberDTO);
                    };
                });
            }
            allDistinctMembers.addAll(extSubsDetailDTOs);
            knLogger.debug(methodName, "allDistinctMembers :", allDistinctMembers);
            List<String> mdns = new ArrayList<>();
            for (KnCorpSubscriberDTO supervisiorList : allDistinctMembers) {
                mdns.add(supervisiorList.getMdn());
            }
            //Setting the AliasId for External contact.
            Map<String, KnCorpSubsEntitiesDTO> subsEntitiesDetails = getSubsEntitiesDetails(mdns, xdmsHomePttId, persisterTxn);
            knLogger.debug(methodName, "subsEntitiesDetails: ", KnGDPRTemplate.mapKeyMdn(subsEntitiesDetails));

            for (KnCorpSubscriberDTO subsc : allDistinctMembers) {
                if (subsEntitiesDetails.containsKey(subsc.getMdn())) {
                    subsc.setAliasMdn(subsEntitiesDetails.get(subsc.getMdn()).getAliasMdn());
                    subsc.setUserId(subsEntitiesDetails.get(subsc.getMdn()).getUserId());
                    subsc.setSubsActiveFS2(subsEntitiesDetails.get(subsc.getMdn()).getActiveFs2());
                    subsc.setSubsActiveFS1(subsEntitiesDetails.get(subsc.getMdn()).getActiveFs1());
                    }
            }
            knLogger.debug(methodName, "allDistinctMembers After ufmi population :", allDistinctMembers);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return allDistinctMembers;
    }

    /**
     * This method returns the distinct profileId List for MDN in extSubslist.
     *
     * @param extSubsList
     * @param xdmHome
     * @param persisterTxn
     * @return Map<Integer, KnExtProfileDetails>
     * @throws KnCorpBOException
     */
    public List<Integer> getExtSubsrProfilelist(List<String> extSubsList, String xdmHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHome);
            return corpXdmDao.getExtSubsrProfilelist(extSubsList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnCorpSubscriberDTO> getSubsribersCorporateDetails(Collection<String> mdnList,
                                                                          String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubsribersCorporateDetails(Collection<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        Map<String, KnCorpSubscriberDTO> subscDBDetails = null;
        try {
            subscDBDetails = corpXdmDao.getSubsribersCorporateDetails(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscribers corporate Details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT :");
        return subscDBDetails;
    }

    public Collection<KnCorpGroupMemberDTO> getMemDetsils(int groupId, String mdn, KnPersisterTxn persisterTxn, String pttServerId)
            throws KnCorpBOException {
        final String methodName = "getMemDetsils(int groupId, String mdn , KnPersisterTxn persisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        Collection<KnCorpGroupMemberDTO> subscDBDetails = null;
        try {
            subscDBDetails = corpXdmDao.getMemDetsils(groupId, mdn, persisterTxn, pttServerId);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscribers corporate Details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT :" , subscDBDetails);
        return subscDBDetails;
    }

    /**
     * This method to the get the valid internal and external subscribers in the corporation from MDN list.
     *
     * @param mdnList
     * @param corpId
     * @param pttServerId
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public KnCorpBCGrpPersistDTO getCorpSubscrDetails(Collection<String> mdnList, int corpId, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getSubsribersCorporateDetails(Collection<String>, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        KnCorpBCGrpPersistDTO corpBCGrpPersistDTO = new KnCorpBCGrpPersistDTO();
        List subscrList;
        List<String> groupMdnList = new ArrayList<String>();
        List<String> interOpMem = new ArrayList<String>();
        try {
            if (null != mdnList) {
                subscrList = new ArrayList(mdnList);
                List<KnCorpSubscriberDTO> subscriberDTOs = corpXdmDao.getCorpSubscrDetails(subscrList, corpId, persisterTxn);
                List<KnCorpSubscriberDTO> interSubscrList = new ArrayList<>();
                List<KnCorpSubscriberDTO> extSubscrList = new ArrayList<>();
                List<String> tpMdnLst = new ArrayList<>();
                for (KnCorpSubscriberDTO subscriberDTO : subscriberDTOs) {
                    if (subscriberDTO.isExternalContact()) {
                        extSubscrList.add(subscriberDTO);
                    } else {
                        interSubscrList.add(subscriberDTO);

                        int clientType = subscriberDTO.getClientType();
                        if (subscriberDTO.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value()
                                || subscriberDTO.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value()) {
                            corpBCGrpPersistDTO.setGrpMdnPresent(Boolean.TRUE);
                            groupMdnList.add(subscriberDTO.getMdn());
                            corpBCGrpPersistDTO.setOnlyAddedGroupMdn(true);

                        } else if (subscriberDTO.getClientType() == KnConstants.INTER_OP_CLIENT_TYPE) {
                            interOpMem.add(subscriberDTO.getMdn());

                        }  else if (clientType == SUBSCR_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value()
                        		|| clientType == SUBSCR_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value()
                        		|| clientType == SUBSCR_CLIENT_TYPE.MOBILEAPI.value()) {
                        	tpMdnLst.add(subscriberDTO.getMdn());
                        }
                    }
                }
                corpBCGrpPersistDTO.setGroupMdnList(groupMdnList);
                corpBCGrpPersistDTO.setValidInternalCont(interSubscrList);
                corpBCGrpPersistDTO.setValidExtContacts(extSubscrList);
                corpBCGrpPersistDTO.setInterOpMembers(interOpMem);
                corpBCGrpPersistDTO.setTpMdnList(tpMdnLst);
            }

        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscribers corporate Details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT :");
        return corpBCGrpPersistDTO;
    }

    /**
     * This method is to return the subscribers Broadcast Group Service feature.
     *
     * @param mdnList
     * @param corpId
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Map<String, Boolean> getSubscrBCGrpBit(List<String> mdnList, int corpId, String xdmsHome, KnPersisterTxn
            persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscrBCGrpBit(List<String>, int, String, KnPersisterTxn)";
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getSubscrBCGrpBit(mdnList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscribers corporate Details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * @param mdn
     * @param corpid
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public boolean isExternalContExist(String mdn, int corpid, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        return isExternalContExist(mdn, corpid, xdmsHome, false, persisterTxn);
    }

    public boolean isExternalContExist(String mdn, int corpid, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.isExternalContExist(mdn, corpid, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<KnMDNInfoDto> getSubscrReverseContacts(ArrayList<Integer> integers, String
            xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            return corpXdmDao.getSubscrReverseContacts(integers, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> getGroupMdns(List<String> removedMemberList, int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getGroupMdnInList(removedMemberList, corpId,FALSE, persisterTxn);

        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }

    public List<KnCorpSubscriberDTO> getGroupMdnInListDTO(List<String> removedMemberList, int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getGroupMdnInListDTO(removedMemberList, corpId, persisterTxn);

        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }

    /**
     * This method is to return the SG MDN.
     *
     * @param corpId
     * @param client_type
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Collection<String> getMdnSpecificToClient(int corpId, int client_type, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getMdnSpecificToClient(corpId, client_type, persisterTxn);

        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }

    /**
     * This method is to return the SG MDN/SG PAtch.
     *
     * @param corpId
     * @param client_types
     * @param xdmsHome
     * @param persisterTxn
     * @return
     * @throws KnCorpBOException
     */
    public Collection<String> getMdnsSpecificToClients(int corpId, Collection<Integer> client_types, String xdmsHome, boolean readOnly, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getMdnsSpecificToClients(corpId, client_types, readOnly, persisterTxn);

        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }

    /**
     * This method is to determine if there are any SG/SU MDN in the corporate for the gateway icon be diplayed in the CAT UI
     *
     * @param corpId
     * @param xdmsHome
     * @param persisterTxn
     * @return isGWEnabled returns true if there are any SG/SU mdn in the corporate.
     * @throws KnCorpBOException
     */
    public boolean getIsGWEnabledForCorp(int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            return corpXdmDao.getIsGWEnabledForCorp(corpId, persisterTxn);

        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }

    }


    /**
     * This method receives the rsourcelist members from the pocsubsc and the members recieved from the Sp of resource list
     * It then seperates the mdns to the internal and external based on corpid and the subscrs not found in pocsubscrinfo table as external..
     *
     * @param contactMap
     * @param fullContactList
     * @param corpId
     * @return externalSubsc
     */
    public List<String> filterInternalExternalContactsForSubc(Map<String, KnCorpSubscriberDTO> contactMap, List<String> fullContactList, int corpId) {
        List<String> externalSubsc = new ArrayList<>();
        for (String contactMdn : fullContactList) {
            KnCorpSubscriberDTO subscDetails = contactMap.get(contactMdn);
            if (subscDetails != null) {
                if (subscDetails.getCorpId() != corpId) {
                    externalSubsc.add(subscDetails.getMdn());
                }
            }else{
                externalSubsc.add(contactMdn);
            }
        }
        return externalSubsc;
    }



    public Map<String, KnCorpSubscriberDTO> getPocSubscribersDetails(Collection<KnCorpSubscriberDTO> sublistMembers,
                                                                     String xdmsHomePttId,
                                                                     KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getPoCSubscriberExistList(Collection<String>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            List<String> mdnList = new ArrayList<String>();
            for(KnCorpSubscriberDTO subsc : sublistMembers){
                mdnList.add(subsc.getMdn());
            }
            return corpXdmDao.getPoCSubscriberExistMap(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getting the pocSubscriber details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer,Boolean> getClientTypeConfigDetails(String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "getClientTypeConfigDetails(String , KnPersisterTxn)";
        knLogger.debug(methodName,"ENTRY");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getClientTypeConfigDetails(persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrievng the client type configuratio- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Set<Integer> getCorporatePamClientTypes(int corpId, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "getCorporatePamClientTypes(int , KnPersisterTxn)";
        knLogger.debug(methodName,"ENTRY");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO( xdmsHomePttId);
            return corpXdmDao.getCorporatePamClientTypes(corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the PAM account infofor the corporate -  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method get the mdn info
     *
     * @param mdnList
     * @param pttServerId
     * @param persisterTxn
     * @throws KnCorpBOException
     */
    public Collection<KnCorpGroupMemberDTO> getPoCSubsDetails(Collection<KnCorpGroupMemberDTO> memDto, Collection<String> mdnList, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getPoCSubsDetails(Collection<KnCorpGroupMemberDTO>, Collection<String>, KnPersisterTxn)";
        knLogger.debug(methodName,"ENTRY");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            Map<String, Integer> memMap =  corpXdmDao.getPoCSubsDetails(mdnList, persisterTxn);
            if(memMap != null && !memMap.isEmpty()){
                for(KnCorpGroupMemberDTO memDetails : memDto){
                    if(memMap.get(memDetails.getMdn()) != null){
                        memDetails.setClientType(memMap.get(memDetails.getMdn()));
                    }
                }
            }
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the -  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT");
        return memDto;
    }

    /**
     * This method is used to Subscriber which is available in LI_TARGET_INFO table.
     *
     * @param persisterTxn
     * @return
     * @throws Exception
     */
    public Collection<KnCorpLITargetProfile> getLITargetInfo(String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException{
        final String methodName = "getLITargetInfo(int , KnPersisterTxn)";
        knLogger.debug(methodName,"ENTRY");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO( xdmsHomePttId);
            return corpXdmDao.getLITargetInfo(persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the Li Target Subscriber Info -  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method assign the contacts for the supervisiorLocWatcher member. This is common method written for
     * create/modify Standard group. Not applicable for dispatch group.
     *
     * @param locWatchers
     * @param totalMemMDN
     * @param corpId
     * @param maxContactPerSubsc
     * @param etagMap
     * @param xdmHome
     * @param persisterTxn
     * @throws KnException
     */
    public void contactForSupervisiorLocWatcher(Collection<String> locWatchers, Collection<String> totalMemMDN, int corpId,
                                                 int maxContactPerSubsc, Map<String, KnOPDirChgDTO> etagMap,
                                                 String xdmHome, KnPersisterTxn persisterTxn) throws KnException {
        String methodName = "contactForSupervisiorLocWatcher(Collection<String>, Collection<String>, int, int, " +
                "Map<String, KnOPDirChgDTO> ,String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : locWatchers - ", locWatchers, "totalMemMDN - ", KnGDPRTemplate.mdnList(totalMemMDN),
                " corpId - ", corpId, " , maxContactPerSubsc - ", maxContactPerSubsc," etagMap:",etagMap);
        try {

            /*Here getting the required mdnList for contact assignement:
            1. locWatchers 		: totalLocWatcher exists for Group
            2. totalMemMDN 		: totalLocWatcher + totalNonLocWatcher */

            Map<String, Collection<String>> locWatcherContactListMap = new HashMap<>();
            Collection<String> totalLocWatcherContacts = new HashSet<>();

            if(locWatchers != null && !locWatchers.isEmpty()) {
                for (String locMdn : locWatchers) {
                    Collection<String> locWatcherContactList = new ArrayList<>();
                    for (String totalMdn : totalMemMDN) {
                        if (!locMdn.equals(totalMdn)) {
                            locWatcherContactList.add(totalMdn);
                            totalLocWatcherContacts.add(totalMdn);
                        }
                    }
                    locWatcherContactListMap.put(locMdn, locWatcherContactList);
                }
                knLogger.debug(methodName, "locWatcherContactListMap - ", KnGDPRTemplate.mapKeyValueListMdn(locWatcherContactListMap));
                // preparing the totalLocWatcherContacts for fetching the KnCorpSubscriberDTO details for notification purpose.
                //Fetching the details form DG.POCSUBSCRINFO table.
                //Excluding profile mdn while getting subscriber details,to be added as contact.
                Map<String, KnCorpSubscriberDTO> subsDetails = getSubsribersCorporateDetails(totalLocWatcherContacts, corpId, xdmHome, persisterTxn);

                if(subsDetails != null && !subsDetails.isEmpty()) {
                    subsDetails.forEach((subs, subsDto) -> {
                        subsDto.setUa(KnGeneralUtil.getUA(subsDto.getUserAgent(), subsDto.getClientPVmajorVer()));
                    });
                }

                knLogger.debug(methodName, "subsDetails - ", KnGDPRTemplate.mapKeyMdn(subsDetails));
                Set<String> profileMdnList=new HashSet<>();
                Map<String, Collection<KnCorpSubscriberDTO>> locWatcherContactListMapDTO = new HashMap<>();
                Collection<String> mdnForPvtList = new ArrayList<>();
                // Here preparing the Map with the collection of DTO with help of subsDetails.
                // Earlier we were using Collection<String>, now we moved to Collection<KnCorpSubscriberDTO>, because of notification purpose.
                if(!locWatcherContactListMap.isEmpty()) {
                    for (Map.Entry<String, Collection<String>> entry : locWatcherContactListMap.entrySet()) {
                        Collection<String> locWatcherContacts = entry.getValue();
                        Collection<KnCorpSubscriberDTO> subsDto = new ArrayList<>();
                        for (String mdn : locWatcherContacts) {
                            KnCorpSubscriberDTO subscriberDTO = subsDetails.get(mdn);
                            if (!(subscriberDTO.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.POC_DONOR_RADIO.value()
                                    || subscriberDTO.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value()
                                    || subscriberDTO.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value())
                                    &&subscriberDTO.getUserProfileIndex()==0) {
                                subsDto.add(subscriberDTO);
                            }else{
                                profileMdnList.add(mdn);
                            }
                        }
                        locWatcherContactListMapDTO.put(entry.getKey(), subsDto);
                    }
                    //Created a string of collection and stored the all locWatcher mdn, which are going to get the contacts.
                    mdnForPvtList.addAll(locWatcherContactListMapDTO.keySet());
                }
                knLogger.debug(methodName, "locWatcherContactListMapDTO - ", locWatcherContactListMapDTO);
                knLogger.debug(methodName," contact Profile mdns",KnGDPRTemplate.mdnList(profileMdnList));
                //etagMap.keySet().removeAll(profileMdnList);
                //knLogger.debug(methodName," etagMap after removing profile mdn ",etagMap);
                // Now our job is to identify the privateListId for the locWatcherMDN.
                // Because we are going to add contact as private list. So we need to check wheather privateListId is exists in DB for LocWatcher or not.
                // If Exists then we need to seperate it out and will add the new contact to the same ID, because privateListID id cannot be deleted for a MDN.
                // If not Exists, then we will generate the privateListId with the help of freePool id utility framework.
                // and thenafter we will create the privateListId for the respectiveMDN.

                Map<String, Integer> subscPvtListIdMap = getSubscribersPrivateListId(mdnForPvtList, xdmHome, persisterTxn);
                knLogger.debug(methodName, "subscPvtListIdMap - ", subscPvtListIdMap);

                // here we are filtering out the MDN which does not have the privateListID and putting into the noPvtContactListMdn variable.
                Collection<String> noPvtContactListMdn = new ArrayList<String>();
                for (Map.Entry<String, Integer> entry : subscPvtListIdMap.entrySet()) {
                    Integer subscPvtListId = entry.getValue();
                    String mdn = entry.getKey();
                    if (subscPvtListId == KnConstants.NO_PRIVATE_LIST_ID) {
                        noPvtContactListMdn.add(mdn);
                    }
                }
                knLogger.debug(methodName, "noPvtContactListMdn - ", KnGDPRTemplate.mdnList(noPvtContactListMdn));

                // Here we are fetching the privateContactList for those LoCMDN who exists with privateListId.
                Map<String, Collection<KnCorpSubscriberDTO>> subscPvtContactList = getSubscribersPrivateList(subscPvtListIdMap, xdmHome, persisterTxn);

                //Here we are iterating the results, and creating a new MAP, because lets take a MDN having privateListId
                // but does not have the contact; for those MDN we are creating a new ArrayList and putting into the MAP.
                // This will be very useful for filtering out the contacts for Privatelist assignement.
                // Otherwise we would have missed the contact for privateList. HOW ?? Below I will show you with the example.
                Map<String, Collection<KnCorpSubscriberDTO>> locWatcherPvtContactList = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
                for (String locWatcherMdn : mdnForPvtList) {
                    Collection<KnCorpSubscriberDTO> subscList = subscPvtContactList.get(locWatcherMdn);
                    if (subscList == null) {
                        subscList = new ArrayList<KnCorpSubscriberDTO>();
                    }
                    locWatcherPvtContactList.put(locWatcherMdn, subscList);
                }
                knLogger.debug(methodName, "locWatcherPvtContactList - ", locWatcherPvtContactList);

                // Now time came for filtering the privateContactList, if you will go ahead without filtering,
                // then you might face the unique constraint DB error. That will be very unlogical from the kodiak point of view.
                // In previous step I mentioned "HOW" ...!!! Now this is the time to show some logical example.

            /* From the previous step we prepared "locWatcherContactList" a MAP. And put all the locWatcher along with contact.
            There are Four locWatcher M1, M2, M3, M4; but for M4 privateList does not exists. That's why M4 will be having empty contacts(From previous step).
            Now we have two MAP:
            One MAP contact current contacts which are going to add as privateContacts: locWatcherPvtContactList
            Anothet MAP contain all the existing contacts which are already exists in DB: locWatcherContactListMapDTO
            Please refer the method call for the details explanation with example.     */

                Map<String, Collection<KnCorpSubscriberDTO>> memberOfLocWatcherList =
                        commonInfoUtil.filterMemberToBeAddedToLocWatcherPrivateList(locWatcherPvtContactList, locWatcherContactListMapDTO);
                knLogger.debug(methodName, "memberOfLocWatcherList :", memberOfLocWatcherList);

                // Now time to fetch the contactList from DG.CORPCONTACTLIST table. subscContactList will contains contact list of locWatcher.
                Map<String, Collection<KnCorpSubscriberDTO>> subscContactList = getSubscribersContactDeatilsList(mdnForPvtList, xdmHome, persisterTxn);
                knLogger.debug(methodName, "subscContactList :", KnGDPRTemplate.mapKeyMdn(subscContactList));

                // Same procedure here also, we will iterate the MAP and prepare the new MAP because some of the locWatcher
                // may not have the contactList present in DG.CORPCONTACTLIST table. So for them we will create new ArrayList.
                Map<String, Collection<KnCorpSubscriberDTO>> locWatcherContactList = new HashMap<>();
                for (String locWatcherMdn : mdnForPvtList) {
                    Collection<KnCorpSubscriberDTO> mdnList = subscContactList.get(locWatcherMdn);
                    if (mdnList == null) {
                        mdnList = new ArrayList<KnCorpSubscriberDTO>();
                    }
                    locWatcherContactList.put(locWatcherMdn, mdnList);
                }
                knLogger.debug(methodName, "locWatcherContactList :", locWatcherContactList);

                // Here we are re-using the previous filtering method. From here also we need to filter out the existing contacts for locWatcher.
                // So memberOfLocWatcherContactList MAP consists locWatcher and distinct contacts(distinct contact means,
                // the contact which does not exists in DB as locWatcher contactList) for them.
                Map<String, Collection<KnCorpSubscriberDTO>> memberOfLocWatcherContactList =
                        commonInfoUtil.filterMemberToBeAddedToLocWatcherPrivateList(locWatcherContactList, locWatcherContactListMapDTO);
                knLogger.debug(methodName, "memberOfDispatcherContactList :", KnGDPRTemplate.mapKeyMdn(memberOfLocWatcherContactList));

                //Checking total number of contacts that are assigned to a subscriber and if total contact count exceeds maxContactPerSubsc,
                //contact count is not updated for that particular mdn.
                List<String> locMdnList = new ArrayList<>(memberOfLocWatcherContactList.keySet());

                for (String mdn : locMdnList) {
                    Collection<KnCorpSubscriberDTO> mdnValueList = subscContactList.get(mdn);
                    if (mdnValueList != null) {
                        if ((mdnValueList.size() + memberOfLocWatcherContactList.get(mdn).size()) > maxContactPerSubsc) {
                            memberOfLocWatcherContactList.remove(mdn);
                        }
                    } else if (memberOfLocWatcherContactList.get(mdn).size() > maxContactPerSubsc) {
                        memberOfLocWatcherContactList.remove(mdn);
                    }
                }

                // Now time to create privateList ID for locWatcher which does not have any privateListId available in kodiakDB.
                // Please follow the below procedure to create a privateListId for locWatcher as well as dependent DB insertion in diffenert tables.
                for (String mdn : noPvtContactListMdn) {
                    int privateListId = 0;
                    KnCorpSublistDTO corpSublist = new KnCorpSublistDTO();
                    //insert into the DG.CorpListInfo table and DG.POCSubscribers table
                    corpSublist.setCorpId(corpId);
                    corpSublist.setSublistName((KnConstants.PRIVATE_LIST_NAME) + mdn);
                    corpSublist.setSublistType(KnConstants.SUBLIST_TYPE_PRIVATE_CONTACTLIST);
                    corpSublist.setDistributionPolicy(KnConstants.DIST_POLICY_PRIVATE_CONTACTLIST);
                    //create corpList entry
                    KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
                    privateListId = genInfoUtil.retrieveIdForTable(KnConstants.CORP_LIST_TABLE, xdmHome, KnConstants.CORP_LIST_TABLE_PK, false, KnConstants.DUAL_DATA_STORE);
                    corpSublist.setSublistId(privateListId);
                    sublistInfoUtil.createSubListDetails(null, corpSublist, xdmHome, persisterTxn);
                    KnIPCorpSublistSubscDistDTO distDto = new KnIPCorpSublistSubscDistDTO();
                    Collection<String> mdnList = new ArrayList<String>();
                    Collection<Integer> subliListIds = new ArrayList<Integer>();
                    mdnList.add(mdn);
                    subliListIds.add(privateListId);
                    distDto.setMdnList(mdnList);
                    distDto.setSublistIds(subliListIds);
                    pushSublistListToSubscriberList(distDto, xdmHome, persisterTxn);
                    updateSubscrinberCorpListId(mdn, corpSublist, xdmHome, persisterTxn);
                    subscPvtListIdMap.put(mdn, privateListId);
                }

                // Now time to finalize the contactList for the locWatcher from
                // subscPvtContactList(applicable for DG.CORPLISTMEMBER Table) && memberOfLocWatcherContactList(applicable for DG.CORPCONTACTLIST table).
                // Here we are going to create two MAP:
                // One MAP contains ((privateListId & MemberList) : DG.CORPLISTMEMBER) : listMemberMap
                // Another One contains ((locWatcher & ContactList) : DG.CORPCONTACTLIST) : completeContactListMap
                // If we iterate the memberOfLocWatcherContactList then we will get the actual contact and from subscPvtListIdMap we will get the privateListId.
                Map<Integer, Collection<KnCorpSubscriberDTO>> listMemberMap = new HashMap<Integer, Collection<KnCorpSubscriberDTO>>();
                for (String mdn : memberOfLocWatcherContactList.keySet()) {
                    Collection<KnCorpSubscriberDTO> memberList = memberOfLocWatcherList.get(mdn);
                    if (memberList != null && !memberList.isEmpty()) {
                        listMemberMap.put(subscPvtListIdMap.get(mdn), memberList);
                    }
                }

                // Here we are inserting into the DG.CORPLISTMEMBER table respective to PrivateListId.
                sublistInfoUtil.insertCorplistMembers(listMemberMap, xdmHome, persisterTxn);

                // Here updating the DG.CORPCONTACTLIST table for contact new assignament.
                Map<String, Collection<KnCorpSubscriberDTO>> completeContactListMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
                completeContactListMap.putAll(memberOfLocWatcherContactList);
                insertCorpContactListMembers(completeContactListMap, xdmHome, persisterTxn);

                // Updating the total contact count through SP(Stored Procedure) call.
                updateSubscribersContactCount(completeContactListMap.keySet(), maxContactPerSubsc, xdmHome, persisterTxn);

                // Now time to Create the Etag MAP and resourceList update for locWatcher.
                Map<String, KnOPDirChgDTO> etagMapData = updateSubcribersResourceListIndexDoc(completeContactListMap.keySet(), xdmHome, null, persisterTxn);
                if (etagMapData != null && !etagMapData.isEmpty()) {
                    for (String mdn : etagMapData.keySet()) {
                        KnOPDirChgDTO dirChgDto = etagMap.get(mdn);
                        if (dirChgDto != null) {
                            Collection<KnOPDocChgDTO> documents = dirChgDto.getDocChgDTO();
                            Collection<KnOPDocChgDTO> docs = etagMapData.get(mdn).getDocChgDTO();
                            if (docs != null && !docs.isEmpty()) {
                                documents.addAll(etagMapData.get(mdn).getDocChgDTO());
                            }
                            dirChgDto.setDocChgDTO(documents);
                            etagMap.put(mdn, dirChgDto);
                        } else {
                            etagMap.put(mdn, etagMapData.get(mdn));
                        }
                    }
                }

                // Finally, we about to reach the finish line after long "BusinessLogic" journey.
                // We have to create and send the notification. Notification consists of contact info about locWatcher.
                KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, completeContactListMap, null, null, null, null, null, null, null, null, null, null);
            }
            //Catching the Exception, if occured in previous steps.
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while assignContact for locWatcher - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while assignContact for locWatcher - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            throw new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e);
        }
        knLogger.debug(methodName, "EXIT :");
    }

    public Collection<String> getSubscribersContactList(List<String> completeMdnList, int corpId, String xdmHome,
                                                        KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getSubscribersContactList(Collection<String>, int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHome);
            return corpXdmDao.getSubscribersContactList(completeMdnList, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getSubscribersContactList - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public KnCorpSubscriberDTO selectSubsInfo(String mdn, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "selectSubsInfo(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.selectSubsInfo(mdn, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while selectSubsInfo - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public String getExtSubsrProfile(String extSubsList, String xdmHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHome);
            return corpXdmDao.getExtSubsrProfile(extSubsList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnCorpSubsEntitiesDTO> getSubsEntitiesDetails(List<String> mdnList, String xdmsHomePttId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException{
        final String methodName = "getSubsEntitiesDetails(List<String> , KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO( xdmsHomePttId);
            return corpXdmDao.getSubsEntitiesDetails(mdnList,false, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the Subs Entities Details-  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnCorpSubsEntitiesDTO> getSubsEntitiesDetails(List<String> mdnList, String xdmsHomePttId,boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException{
        final String methodName = "getSubsEntitiesDetails(List<String> ,boolean KnPersisterTxn)";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO( xdmsHomePttId);
            return corpXdmDao.getSubsEntitiesDetails(mdnList,readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the Subs Entities Details-  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, String> getMdnByUsingAliasMdn(List<String> aliasMdnList, String xdmsHomePttId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        return getMdnByUsingAliasMdn(aliasMdnList, xdmsHomePttId, false, persisterTxn);
    }

    public Map<String, String> getMdnByUsingAliasMdn(List<String> aliasMdnList, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getMdnByUsingAliasMdn(List<String> ,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getMdnByUsingAliasMdn(aliasMdnList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the Subs Entities Details-  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, String> getMdnByUsingUserId(List<String> userIdList, String xdmsHomePttId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        return getMdnByUsingUserId(userIdList, xdmsHomePttId, false, persisterTxn);
    }

    public Map<String, String> getMdnByUsingUserId(List<String> userIdList, String xdmsHomePttId, boolean readOnly, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getMdnByUsingUserId(List<String> ,boolean, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            return corpXdmDao.getMdnByUsingUserId(userIdList, readOnly, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving the Subs Entities Details-  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void checkValidHierarchySubs(List<String> mdnList, Map<String, Object> customParams, String xdmsHomePttId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "checkValidHierarchySubs(List<String> ,Map<String, Object>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY");
        boolean isValid;
        try {
            if (null == customParams || customParams.isEmpty()
                    || null == customParams.get(IDTYPE) || null == customParams.get(IDLIST)) {
                knLogger.debug(methodName, "Skip Hierarchy validation");
                return;
            }
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            isValid = corpXdmDao.checkValidHierarchySubs(mdnList, customParams, persisterTxn);
            if (!isValid) {
                throw new KnCorpBOValidationException(NON_FIRSTNET_FAN,
                        "Invalid owner context ids", "", "", "", "DataType", "");
            }
        } catch (KnDAOException | KnCorpBOValidationException e) {
            knLogger.error(methodName, "KnDAOException occured while validating hirerchy check-  ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public void subsEtagUpdateUtil(KnSubsProfileDTO mdnProfile, int corpId,String operationType ,String xdmsHomePttId) {
        String methodName = "subsEtagUpdateUtil()";
        knLogger.info(methodName, "ENTRY : Input DTO Passed - ", mdnProfile,corpId,operationType);
        try {
            knLogger.debug(methodName, "xdmsHomePttId - ", KnGDPRTemplate.mcpttId(xdmsHomePttId));
            //ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            List<String> mdnList = new ArrayList<>();
            mdnList.add(mdnProfile.getMdn());
            Collection<String> subscList = getSubscribersContactList(mdnList, corpId, xdmsHomePttId, null);
            Collection<String> subsMDNs = getPocSubscribersDetails(subscList, corpId, xdmsHomePttId, null);
            knLogger.debug(methodName, "subscList:", subscList, "subsMDNs : ", KnGDPRTemplate.mdnList(subsMDNs));
            Map<Integer, Collection<String>> groupMemberDistMap = corpGroupInfoUtil.getAllSubscribersGroupDistForAllCorporate(mdnList,
                    xdmsHomePttId, null);
            knLogger.debug(methodName, "groupMemberDistMap:", groupMemberDistMap);
            //Collection<KnCorpSubscriberDTO> mdnDtoList = new ArrayList<>();
            Set<Integer> groupIds = groupMemberDistMap.keySet();
            ArrayList<KnCorpGroupDTO> groupDetails = corpGroupInfoUtil.getGroupBasicInfoList(groupIds, xdmsHomePttId, null);
            Collection<Integer> groupIdsNonLarge = groupDetails.stream().filter(grpInfo -> !grpInfo.isEmergGrp()).map(KnCorpGroupDTO::getGroupId).collect(Collectors.toList());
            //Map<Integer, Collection<String>> groupDistList = corpGroupInfoUtil.getGroupSubscriberDistList(groupIdsNonLarge, xdmsHomePttId, persisterTxn);
            Collection<String> groupMemberLists = corpGroupInfoUtil.getSubscribersGroupList(groupIdsNonLarge, xdmsHomePttId, null);
            //Set<String> groupDistMem = new HashSet<>();
            //groupDistMem.addAll(groupMemberLists.stream().filter(mdn -> !mdnList.contains(mdn)).collect(Collectors.toSet()));
            Set<String> allReverseMdns = new HashSet<>();
            allReverseMdns.addAll(subsMDNs);
            allReverseMdns.addAll(groupMemberLists);
           /* Map<String, KnCorpSubscriberDTO> contactListMap = corpXdmDao.getSubsribersCorporateDetails(allReverseMdns, corpId, persisterTxn);
            mdnDtoList.addAll(contactListMap.values());*/
            //1. Updating is OSM authorize flag for mdn.
            //2. ResourceList Update: subscList
            //3. Group eTag: groupIdsNonLarge
            //4. Directory eTag: groupDistMem(subscList + filtered groupMemberLists).

            List<String> allowedOSMOperationList=new ArrayList<>();
            allowedOSMOperationList.add(USER_LOGIN);
            allowedOSMOperationList.add(GET_SUBSCRIBER_CONFIG_DOC);
            allowedOSMOperationList.add(UPDATE_SUBSCRIBER);
            int clientType = mdnProfile.getClientType();
            boolean isOSMAuthorizeBit = KnGeneralUtil.getFeatureBitValue(mdnProfile.getSubscriberFS2(), IS_OSM_AUTHORIZE_BIT);
            if(allowedOSMOperationList.contains(operationType)) {
                if ((clientType==DISPATCHER_CLIENT||clientType==THIRD_PARTY_DISPATCHERS_CLIENT)
                        &&isOSMAuthorizeBit) {
                    knLogger.debug(methodName, "isOsmAuth FB enabled and CT(3,12)");
                    corpGroupInfoUtil.updateIsOSMAuthorize(groupIds, mdnProfile.getMdn(), "1", xdmsHomePttId, null);
                } else if (!isOSMAuthorizeBit) {
                    knLogger.debug(methodName, "isOsmAuth FB disabled.");
                    corpGroupInfoUtil.updateIsOSMAuthorize(groupIds, mdnProfile.getMdn(), "0", xdmsHomePttId, null);
                } else {
                    knLogger.debug(methodName, "neither isOsmAuth FB enabled and CT(3,12) nor isOsmAuth FB disabled.");
                }
            }

            //INT-95992 - Optimization The batch update is costly job, so transaction for batch update for etag management is controlled over the dao layer
            updateSubcribersResourceListIndexDoc(subsMDNs, xdmsHomePttId, null, null);
            corpGroupInfoUtil.updateGroupListEtag(groupIdsNonLarge, xdmsHomePttId,  null);
            corpSubsProvInfoUtil.updateDirectoryEtag(allReverseMdns, xdmsHomePttId, null, null);



            knLogger.debug(methodName, "Successfully updated the eTag:");
           /* Map<Integer, Integer> groupEtagMap = corpGroupInfoUtil.updateGroupListEtag(groupIdsNonLarge, xdmsHomePttId, persisterTxn);
            etagMap = corpSubsProvInfoUtil.updateDirectoryEtag(groupMemberLists, xdmsHomePttId, etagMap, persisterTxn); // Update Directory Etag for Group Member
            etagMap = updateSubcribersImpactedTables(xdmsHomePttId, subsMDNs, mdnDtoList, persisterTxn, null); // Update Directory and Resource for contacts
            etagMap = commonInfoUtil.formSubscriberNotification(groupDistList, groupEtagMap,
                    com.kodiak.xdms.server.common.resources.KnConstants.DOC_CHANGE_TYPE.REPLACE.value(), etagMap, null); // preparing groupNotify.
            etagMap = KnCorpCommonInfoUtil.formXcapDiffNotificationForActivefs(etagMap, mdnProfile);
            knLogger.debug(methodName, "Successfully updated the eTag:", etagMap);*/
        } catch (Exception e) {
            knLogger.error(methodName, "Exception occured while updating subs etag",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
        }
        knLogger.debug(methodName, "EXIT:");
    }

    public Map<String, Collection<KnCorpSubscriberDTO>> contactListPreparationForNonDispatcher(String ipMdn, Collection<String> totalMemMDN, LinkedList<String> contactListMembers, int corpId,
                                                                                               int maxContactPerSubsc, Map<String, KnOPDirChgDTO> etagMap,
                                                                                               String xdmHome, KnPersisterTxn persisterTxn) throws KnException {
        String methodName = "contactListPreparationForNonDispatcher(Collection<String>, Collection<String>, int, int, " +
                "Map<String, KnOPDirChgDTO> ,String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ipMdn - ", KnGDPRTemplate.mdn(ipMdn), "totalMemMDN - ", totalMemMDN == null ? totalMemMDN : KnGDPRTemplate.mdnList(totalMemMDN), " corpId - ", corpId,
                " , maxContactPerSubsc - ", maxContactPerSubsc);
        Map<String, Collection<KnCorpSubscriberDTO>> finalContactListMap = null;
        if (totalMemMDN != null && !totalMemMDN.isEmpty()) {
            totalMemMDN.add(ipMdn);
            Map<String, KnCorpSubscriberDTO> subsDetails = getSubsribersCorporateDetails(totalMemMDN, corpId, xdmHome, persisterTxn);
            knLogger.debug(methodName, "subsDetails - ", KnGDPRTemplate.mapKeyMdn(subsDetails));
            Map<String, KnCorpSubscriberDTO> intSubsDetails = subsDetails.entrySet().stream().filter(map -> map.getValue().getContact_type() == 0)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            knLogger.debug(methodName, "subsDetails Size - ", subsDetails.size(), intSubsDetails.size());
            Collection<String> locWatcher = new ArrayList<>();
            Collection<String> dispatcher = new ArrayList<>();
            Map<String, Collection<String>> contactListMap = new HashMap<>();
            Collection<String> contList = new ArrayList<>();
            contList.add(ipMdn);
            intSubsDetails.forEach((key, value) -> {
                if (!key.equals(ipMdn)) {
                    contactListMap.put(key, contList);
                    if (value.getClientType() != com.kodiak.common.resources.KnConstants.SUBS_CLIENT_TYPE.DISPATCH_CLIENT.value()) {
                        locWatcher.add(key);
                    } else {
                        dispatcher.add(key);
                    }
                }
            });
            contactListMap.put(ipMdn, dispatcher);
            LinkedHashMap<String, LinkedList<String>> deleteContactListMap = new LinkedHashMap<String, LinkedList<String>>();
            deleteContactListMap.put(ipMdn, contactListMembers);
            knLogger.debug(methodName, "locWatcher - ", locWatcher, "dispatcher: ", dispatcher, "contactListMap: ", contactListMap, "deleteContactListMap :", deleteContactListMap);
            finalContactListMap = contactForBulkGroup(intSubsDetails, deleteContactListMap, contactListMap, corpId, maxContactPerSubsc, etagMap, xdmHome, persisterTxn);
        }
        return finalContactListMap;
    }

    public Map<String, Collection<KnCorpSubscriberDTO>> contactListPreparationForDispatcher(String ipMdn, Collection<String> totalMemMDN,
                                                                                            int corpId, int maxContactPerSubsc,
                                                                                            Map<String, KnOPDirChgDTO> etagMap,
                                                                                            String xdmHome, KnPersisterTxn persisterTxn) throws KnException {
        String methodName = "contactListPreparationForDispatcher(Collection<String>, Collection<String>, int, int, Map<String, KnOPDirChgDTO> ,String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ipMdn - ", KnGDPRTemplate.mdn(ipMdn), "totalMemMDN - ", KnGDPRTemplate.mdnList(totalMemMDN), " corpId - ", corpId,
                " , maxContactPerSubsc - ", maxContactPerSubsc);
        Map<String, Collection<KnCorpSubscriberDTO>> finalContactListMap = null;
        if(totalMemMDN != null && !totalMemMDN.isEmpty()){
            totalMemMDN.add(ipMdn);
            Map<String, KnCorpSubscriberDTO> subsDetails = getSubsribersCorporateDetails(totalMemMDN, corpId, xdmHome, persisterTxn);
            knLogger.debug(methodName, "subsDetails - ", KnGDPRTemplate.mapKeyMdn(subsDetails));
            Map<String, KnCorpSubscriberDTO> intSubsDetails = subsDetails.entrySet().stream().filter(map -> map.getValue().getContact_type() == 0)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            knLogger.debug(methodName, "subsDetails Size - ", subsDetails.size(), intSubsDetails.size());
            Map<String, Collection<String>> contactListMap = new HashMap<>();
            Collection<String> contList = new ArrayList<>();
            contList.add(ipMdn);
            totalMemMDN.forEach(mdn -> {
                if(intSubsDetails.containsKey(mdn) && !mdn.equals(ipMdn)) contactListMap.put(mdn, contList);
            });
            contactListMap.put(ipMdn, totalMemMDN);
            knLogger.debug(methodName, "contactListMap - ", KnGDPRTemplate.mapKeyMdn(contactListMap));
            finalContactListMap = contactForBulkGroup(intSubsDetails, null, contactListMap, corpId, maxContactPerSubsc, etagMap, xdmHome, persisterTxn);
        }
        return finalContactListMap;
    }

    public Map<String, Collection<KnCorpSubscriberDTO>> contactForBulkGroup(Map<String, KnCorpSubscriberDTO> subsDetails,
                                                                            LinkedHashMap<String, LinkedList<String>> deleteContactListMap,
                                                                            Map<String, Collection<String>> contactListMap,
                                                                            int corpId, int maxContactPerSubsc, Map<String, KnOPDirChgDTO> etagMap,
                                                                            String xdmHome, KnPersisterTxn persisterTxn) throws KnException {
        String methodName = "contactForBulkGroup(Collection<String>, Collection<String>, int, int, " +
                "Map<String, KnOPDirChgDTO> ,String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : subsDetails - ", subsDetails == null ? subsDetails : KnGDPRTemplate.mapKeyMdn(subsDetails),
                "contactListMap - ", contactListMap, " corpId - ", corpId, " , maxContactPerSubsc - ", maxContactPerSubsc);
        Map<String, Collection<KnCorpSubscriberDTO>> completeContactListMap = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
        try {
            if (contactListMap != null && !contactListMap.isEmpty()) {
                if (!subsDetails.isEmpty()) {
                    subsDetails.forEach((subs, subsDto) -> {
                        subsDto.setUa(KnGeneralUtil.getUA(subsDto.getUserAgent(), subsDto.getClientPVmajorVer()));
                    });
                }
                Map<String, Collection<KnCorpSubscriberDTO>> contactListMapDTO = new HashMap<>();
                Collection<String> mdnForPvtList = new ArrayList<>();
                if (!contactListMap.isEmpty()) {
                    for (Map.Entry<String, Collection<String>> entry : contactListMap.entrySet()) {
                        Collection<String> contacts = entry.getValue();
                        Collection<KnCorpSubscriberDTO> subsDto = new ArrayList<>();
                        for (String mdn : contacts) {
                            KnCorpSubscriberDTO subscriberDTO = subsDetails.get(mdn);
                            if (!(subscriberDTO.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.POC_DONOR_RADIO.value()
                                    || subscriberDTO.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.GROUPMDN.value()
                                    || subscriberDTO.getClientType() == KnConstants.SUBSCR_CLIENT_TYPE.SGMDNPATCH.value())) {
                                subsDto.add(subscriberDTO);
                            }
                        }
                        contactListMapDTO.put(entry.getKey(), subsDto);
                    }
                    mdnForPvtList.addAll(contactListMapDTO.keySet());
                }
                deleteMembersFromCorpContactList(deleteContactListMap, xdmHome, persisterTxn);
                knLogger.debug(methodName, "contactListMapDTO - ", contactListMapDTO);
                Map<String, Integer> subscPvtListIdMap = getSubscribersPrivateListId(mdnForPvtList, xdmHome, persisterTxn);
                knLogger.debug(methodName, "subscPvtListIdMap - ", KnGDPRTemplate.mapKeyMdn(subscPvtListIdMap));
                Collection<String> noPvtContactListMdn = new ArrayList<String>();
                noPvtContactListMdn.addAll(subscPvtListIdMap.entrySet().stream().filter(map -> map.getValue() == KnConstants.NO_PRIVATE_LIST_ID)
                        .map(Map.Entry::getKey).collect(Collectors.toList()));
                knLogger.debug(methodName, "noPvtContactListMdn - ", KnGDPRTemplate.mdnList(noPvtContactListMdn));
                Map<String, Collection<KnCorpSubscriberDTO>> subscPvtContactList = getSubscribersPrivateList(subscPvtListIdMap, xdmHome, persisterTxn);
                Map<String, Collection<KnCorpSubscriberDTO>> pvtContactList = new HashMap<String, Collection<KnCorpSubscriberDTO>>();
                mdnForPvtList.forEach(pvtMdn -> {
                    Collection<KnCorpSubscriberDTO> subscList = subscPvtContactList.get(pvtMdn);
                    if (subscList == null) {
                        subscList = new ArrayList<KnCorpSubscriberDTO>();
                    }
                    pvtContactList.put(pvtMdn, subscList);
                });
                knLogger.debug(methodName, "pvtContactList - ", KnGDPRTemplate.mapKeyMdn(pvtContactList));
                Map<String, Collection<KnCorpSubscriberDTO>> memberOfMdnList =
                        commonInfoUtil.filterMemberToBeAddedToLocWatcherPrivateList(pvtContactList, contactListMapDTO);
                knLogger.debug(methodName, "memberOfMdnList :", KnGDPRTemplate.mapKeyMdn(memberOfMdnList));
                Map<String, Collection<KnCorpSubscriberDTO>> subscContactList = getSubscribersContactDeatilsList(mdnForPvtList, xdmHome, persisterTxn);
                knLogger.debug(methodName, "subscContactList :", KnGDPRTemplate.mapKeyMdn(subscContactList));
                Map<String, Collection<KnCorpSubscriberDTO>> subsContactList = new HashMap<>();
                mdnForPvtList.forEach(pvtMdn -> {
                    Collection<KnCorpSubscriberDTO> mdnList = subscContactList.get(pvtMdn);
                    if (mdnList == null) {
                        mdnList = new ArrayList<KnCorpSubscriberDTO>();
                    }
                    subsContactList.put(pvtMdn, mdnList);
                });
                knLogger.debug(methodName, "subsContactList :", KnGDPRTemplate.mapKeyMdn(subsContactList));
                Map<String, Collection<KnCorpSubscriberDTO>> memberOfContactList =
                        commonInfoUtil.filterMemberToBeAddedToLocWatcherPrivateList(subsContactList, contactListMapDTO);
                knLogger.debug(methodName, "memberOfContactList :", KnGDPRTemplate.mapKeyMdn(memberOfContactList));
                for (String mdn : noPvtContactListMdn) {
                    int privateListId = 0;
                    KnCorpSublistDTO corpSublist = new KnCorpSublistDTO();
                    corpSublist.setCorpId(corpId);
                    corpSublist.setSublistName((KnConstants.PRIVATE_LIST_NAME) + mdn);
                    corpSublist.setSublistType(KnConstants.SUBLIST_TYPE_PRIVATE_CONTACTLIST);
                    corpSublist.setDistributionPolicy(KnConstants.DIST_POLICY_PRIVATE_CONTACTLIST);
                    KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
                    privateListId = genInfoUtil.retrieveIdForTable(KnConstants.CORP_LIST_TABLE, xdmHome, KnConstants.CORP_LIST_TABLE_PK,
                            false, KnConstants.DUAL_DATA_STORE);
                    corpSublist.setSublistId(privateListId);
                    sublistInfoUtil.createSubListDetails(null, corpSublist, xdmHome, persisterTxn);
                    KnIPCorpSublistSubscDistDTO distDto = new KnIPCorpSublistSubscDistDTO();
                    Collection<String> mdnList = new ArrayList<String>();
                    Collection<Integer> subliListIds = new ArrayList<Integer>();
                    mdnList.add(mdn);
                    subliListIds.add(privateListId);
                    distDto.setMdnList(mdnList);
                    distDto.setSublistIds(subliListIds);
                    pushSublistListToSubscriberList(distDto, xdmHome, persisterTxn);
                    updateSubscrinberCorpListId(mdn, corpSublist, xdmHome, persisterTxn);
                    subscPvtListIdMap.put(mdn, privateListId);
                }
                Map<Integer, Collection<KnCorpSubscriberDTO>> listMemberMap = new HashMap<Integer, Collection<KnCorpSubscriberDTO>>();
                for (String mdn : memberOfContactList.keySet()) {
                    Collection<KnCorpSubscriberDTO> memberList = memberOfMdnList.get(mdn);
                    if (memberList != null && !memberList.isEmpty()) {
                        listMemberMap.put(subscPvtListIdMap.get(mdn), memberList);
                    }
                }
                sublistInfoUtil.insertCorplistMembers(listMemberMap, xdmHome, persisterTxn);
                completeContactListMap.putAll(memberOfContactList);
                insertCorpContactListMembers(completeContactListMap, xdmHome, persisterTxn);
                updateSubscribersContactCount(completeContactListMap.keySet(), maxContactPerSubsc, xdmHome, persisterTxn);
                updateSubscribersDirectory(completeContactListMap.keySet(), etagMap, xdmHome, persisterTxn);
                Map<String, KnOPDirChgDTO> etagMapData = updateSubcribersResourceListIndexDoc(completeContactListMap.keySet(),
                        xdmHome, etagMap, persisterTxn);
                if (etagMapData != null && !etagMapData.isEmpty()) {
                    for (String mdn : etagMapData.keySet()) {
                        KnOPDirChgDTO dirChgDto = etagMap.get(mdn);
                        if (dirChgDto != null) {
                            Collection<KnOPDocChgDTO> documents = dirChgDto.getDocChgDTO();
                            Collection<KnOPDocChgDTO> docs = etagMapData.get(mdn).getDocChgDTO();
                            if (docs != null && !docs.isEmpty()) {
                                documents.addAll(etagMapData.get(mdn).getDocChgDTO());
                            }
                            dirChgDto.setDocChgDTO(documents);
                            etagMap.put(mdn, dirChgDto);
                        } else {
                            etagMap.put(mdn, etagMapData.get(mdn));
                        }
                    }
                }
                KnCorpCommonInfoUtil.formXcapDiffNotification(etagMap, completeContactListMap, null,
                        null, null, null, null,
                        null, null, null, null, null);
            }
        } catch (KnCorpBOException e) {
            knLogger.error(methodName, "KnCorpBOException occured while assignContact for locWatcher - ", e);
            throw e;
        } catch (Exception e) {
            knLogger.error(methodName, "Unexpected exception occured while assignContact for locWatcher - ",
                    new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e));
            throw new KnException(KnErrorCodes.BOEntity.INTERNAL_ERROR, e.getMessage(), e);
        }
        knLogger.debug(methodName, "EXIT :");
        return completeContactListMap;
    }

    public Map<Integer,Integer> getUpIndexCountMap(Collection<Integer> userprofileIndexes, Integer corpId, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {

        final String methodName = "getUpIndexCountMap(String, Integer, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            return corpXdmDao.getUpIndexCountMap(userprofileIndexes, corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while getSubscribersCount - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public static List<KnCorpGroupMemberDTO> transformContactToGroupObj(List<KnCorpContactDTO> modifyMemberList){
        List<KnCorpGroupMemberDTO> groupMemberList=new ArrayList<>();

        if(modifyMemberList!=null){
            for(KnCorpContactDTO members:modifyMemberList){
                KnCorpGroupMemberDTO grpMemInfo=new KnCorpGroupMemberDTO();
                grpMemInfo.setMdn(members.getMdn());
                grpMemInfo.setSupervisory(members.getSupervisory());
                grpMemInfo.setLocWatcher(members.getLocWatcher());
                grpMemInfo.setCallInitiatePermission(members.getCallInitiatePermission());
                grpMemInfo.setCallReceivePermission(members.getCallReceivePermission());
                grpMemInfo.setInCallPermission(members.getInCallPermission());
                grpMemInfo.setVideoCallInitiatePermission(members.getVideoCallInitiatePermission());
                grpMemInfo.setVideoCallReceivePermission(members.getVideoCallReceivePermission());
                grpMemInfo.setVideoInCallPermission(members.getVideoInCallPermission());
                grpMemInfo.setGroupModifyPerm(members.getGroupModifyPerm());
                grpMemInfo.setIsOSMAuthorize(members.getIsOSMAuthorize());
                groupMemberList.add(grpMemInfo);
            }
        }
        return groupMemberList;
    }

    public Map<String, KnOPDirChgDTO> updateDistinctSubcribersDirectoryBCG(Collection<String> mdnList,
                                                                        Collection<Integer> groupIdLst,
                                                                        Map<String, KnOPDirChgDTO> eTags, String xdmsHomePttId,
                                                                        KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateDistinctSubcribersDirectoryBCG(Collection<String>,  Collection<Integer>, Map<String, KnOPDirChgDTO>, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            Map<String, KnOPDirChgDTO> notificationMap = eTags;
            //added null checks to avoid unwanted calls
            if ((mdnList != null && !mdnList.isEmpty())||(groupIdLst != null && !groupIdLst.isEmpty())) {
                notificationMap = corpXdmDao.updateDistinctSubcribersDirectoryBCG(mdnList, groupIdLst,
                        eTags, persisterTxn,xdmsHomePttId);
                KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
                notificationMap = commonInfoUtil.setXapRootUri(notificationMap, xdmsHomePttId, persisterTxn);
            }
            return eTags;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updateSubcribersDirectory - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public List<String> getOwnCorpRegroupMembers(int corpId,String xdmsHomePttId,KnPersisterTxn persisterTxn)throws KnCorpBOException {
        final String methodName="getOwnCorpRegroupMembers()";
        List<String> regroupMdns =new ArrayList<>();
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            regroupMdns = corpXdmDao.getOwnCorpRegroupMembers(corpId, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return regroupMdns;
    }

    public Map<String, KnOPDirChgDTO> updateSetSubcribersDirectory(Collection<String> mdnList,
                                                                        Map<String, KnOPDirChgDTO> eTags, String xdmsHomePttId,
                                                                        KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateSetSubcribersDirectory()";
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
            //Map<String, KnOPDirChgDTO> notificationEtagMap = new HashMap<>();
            if (mdnList != null && !mdnList.isEmpty()) {
                Map<String, KnOPDirChgDTO> notificationMap = corpXdmDao.updateSetSubcribersDirectory(mdnList,
                        eTags, persisterTxn);
                KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
                eTags = commonInfoUtil.setXapRootUri(notificationMap, xdmsHomePttId, persisterTxn);
            }
            return eTags;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String,Integer> getUserProfileOwnerinfo(ArrayList<String> profileIds, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getUserProfileOwnerinfo()";
        knLogger.debug(methodName, "ENTRY :");
        Map<String, Integer> result=null;
        try {
            ICorpXdmDAO xdmDAO = new KnCorpXdmDAO(xdmsHome);
            result= xdmDAO.getUserProfileOwnerinfo(profileIds,false, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return result;
    }

    public Set<Integer> getCorpIdFromMdnList(List<String> mdnList, String pttServerId, KnPersisterTxn persisterTxn)
            throws KnCorpBOException {
        final String methodName = "getCorpIdFromMdnList(List<String>, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        Set<Integer> corpIds = new HashSet<>();

        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
        try {
            corpIds = corpXdmDao.getCorpIdFromMdnList(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while retrieving subscribers corporate Details - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        knLogger.debug(methodName, "EXIT : Subscriber corporate details returned - ",corpIds);
        return corpIds;
    }

    public Map<String, KnOPDirChgDTO> updateRegroupSubscribersDirectory(Collection<String> mdnList,
                                                                 Map<String, KnOPDirChgDTO> etagMap, String xdmsHomePttId,
                                                                 KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "updateRegroupSubscribersDirectory(Collection<String>,  Map<String, KnOPDirChgDTO>, String, KnPersisterTxn)";
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        try {
            etagMap = corpXdmDao.updateRegroupSubscribersDirectory(mdnList, etagMap, persisterTxn);
            KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
            etagMap = commonInfoUtil.setXapRootUri(etagMap, xdmsHomePttId, persisterTxn);
            return etagMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while deleting external contacts - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public boolean ifMdnisSGMDN(String mdn,String xdmsHome,KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "ifMdnisSGMDN(mdn,persisterTxn)";
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        boolean ifMdnisSGMDN = false;
        knLogger.debug(methodName, "ENTRY: ");
        try{
            ifMdnisSGMDN = corpXdmDao.ifMdnisSGMDN(mdn,persisterTxn);
        } catch (KnDAOException e ){
            knLogger.error(methodName, "KnDAOException occured while checking ifMdnisSGMDN - ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
        return ifMdnisSGMDN;
    }

    public Map<Integer, List<KnCorpSubscriberDTO>> getBulkDistinctMembersList(HashMap<Integer, List<KnCorpSubscriberDTO>> mdnsToBeAddedToPrivateListMap,
                                                                                    Map<Integer, List<KnCorpSubscriberDTO>> distAddedSublistMemLstMap,
                                                                                    int corpId, String xdmsHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getBulkDistinctMembersList(mdn,persisterTxn)";
        knLogger.debug(methodName, "mdnsToBeAddedToPrivateListMap:", mdnsToBeAddedToPrivateListMap, "distAddedSublistMemLstMap:", distAddedSublistMemLstMap, "corpId:", corpId);
        Map<Integer, List<KnCorpSubscriberDTO>> returnMap = new HashMap<>();
        for(var entry : mdnsToBeAddedToPrivateListMap.entrySet())
        {
            List<KnCorpSubscriberDTO> mdnsToBeAddedToPrivateList = entry.getValue();
            List<KnCorpSubscriberDTO> distAddedSublistMemLst = distAddedSublistMemLstMap.get(entry.getKey());
            if(distAddedSublistMemLst == null)
            {
                distAddedSublistMemLst = new ArrayList<>();
            }
            var returnData = getDistinctMembersList(mdnsToBeAddedToPrivateList, distAddedSublistMemLst, corpId, xdmsHome, persisterTxn);

            returnMap.put(entry.getKey(), returnData.stream().toList());
        }
        knLogger.debug(methodName, "returnMap: ", returnMap);

        return returnMap;
    }
    /**
     * This method returns the distinct profileId List for MDN in extSubslist.
     *
     * @param extSubsList
     * @param xdmHome
     * @param persisterTxn
     * @return Map<Integer, KnExtProfileDetails>
     * @throws KnCorpBOException
     */
    public  Map<String, Integer> getExtSubsrProfilelistMap(List<String> extSubsList, String xdmHome, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmHome);
            return corpXdmDao.getExtSubsrProfilelistMap(extSubsList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<String, KnOPDirChgDTO> updateSubcribersEtagTables(String pttServerId, Collection<String> mdnList,
                                                                     KnPersisterTxn persisterTxn, Map<String, KnOPDirChgDTO> etagMap) throws KnCorpBOException {
        final String methodName = "updateSubcribersEtagTables(String,Collection<String>,KnPersisterTxn, Map<String, KnOPDirChgDTO>)";
        knLogger.debug(methodName, "ENTRY :");
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            Map<String, KnOPDirChgDTO> notificationMap = new HashMap<String, KnOPDirChgDTO>();
            //added null checks to avoid unwanted calls
            if (mdnList != null && !mdnList.isEmpty()) {
                notificationMap = corpXdmDao.updateSubcribersEtagTables(mdnList, persisterTxn, etagMap);
               // KnCorpCommonInfoUtil commonInfoUtil = new KnCorpCommonInfoUtil();
                //notificationMap = commonInfoUtil.setXapRootUri(notificationMap, pttServerId, persisterTxn);
            }
            return notificationMap;
        } catch (KnDAOException e) {
            knLogger.error(methodName, "KnDAOException occured while updating the subscribers impacted tables",
                    " due to some contact changes- ", e);
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method deleted the contactlist entries for mdnList.
     *
     * @param mdnList
     * @param pttServerId
     * @param persisterTxn
     * @throws KnCorpBOException
     */
    public void deleteCorpContactMdnList(List<String> mdnList, String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.deleteCorpContactMdnList(mdnList, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    /**
     * This method will update the locawatcher mdn.
     *
     * @param mdn
     * @param locwatcher
     * @param pttServerId
     * @param persisterTxn
     * @throws KnCorpBOException
     */
    public void updateLocwatcherMdn(String mdn, int locwatcher,String pttServerId, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        try {
            ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(pttServerId);
            corpXdmDao.updateLocwatcherMdn(mdn,locwatcher, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Collection<String> getProfileMdnsForDisContact(Collection<String> members, String localPttId, int corpId, int disMemCheck, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getProfileMdnsForDisContact(Collection<String>, String, int,int, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : members - ", KnGDPRTemplate.mdnList(members), " localPttId - ", KnGDPRTemplate.mcpttId(localPttId),
                " corpId - ", corpId, " disMemCheck - ", disMemCheck);
        Map<String, KnCorpSubscriberDTO> baseMdn = getOnlyBaseMdn(new ArrayList<>(members), localPttId, corpId, false, persisterTxn);
        List<String> profileMdns = new ArrayList<>();
        List<String> baseMdnList = new ArrayList<>(baseMdn.keySet());
        Collection<String> dispatchMemberContacts = new HashSet<>();
        dispatchMemberContacts = getDispContacts(baseMdnList, corpId, localPttId, persisterTxn);
        if (disMemCheck == DISABLED) {
            if (!dispatchMemberContacts.isEmpty()) {
                profileMdns = getProfileMdnsByMcids(dispatchMemberContacts, localPttId, disMemCheck, persisterTxn);
            }
        } else if (disMemCheck == ENABLED) {
            baseMdnList.removeAll(dispatchMemberContacts);
            profileMdns = getProfileMdnsByMcids(baseMdnList, localPttId, disMemCheck, persisterTxn);
        }

        knLogger.debug(methodName, "EXIT : profileMdns - ", KnGDPRTemplate.mdnList(profileMdns));
        return profileMdns;
    }

    public List<String> getProfileMdnsByMcids(Collection<String> mdnList, String xdmsHome, int dispMemCheck, KnPersisterTxn persisterTxn) throws KnCorpBOException {
        final String methodName = "getProfileMdnsByMcids(Collection<String>, String, int,int, KnPersisterTxn)";
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHome);
        try {
            knLogger.debug(methodName, "Entry");
            return corpXdmDao.getProfileMdnsByMcids(mdnList, dispMemCheck, persisterTxn);
        } catch (KnDAOException e) {
            throw new KnCorpBOException(e.getErrorCode(), e.getErrorMessage(), e);
        }
    }

    public Map<Integer, String> getCorpIdListWhereIsExternalContact(List<String> mdnList, String xdmsHomePttId, KnPersisterTxn persisterTxn) throws KnCorpBOException,KnDAOException {
        final String methodName = "getCorpIdListWhereIsExternalContact(String, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        return corpXdmDao.getCorpIdListWhereIsExternalContact(mdnList, persisterTxn);
    }

    public Collection<KnCorpSublistDTO> getSubscMappedSublistList(Map<String, KnIPCorpContactDTO> contactDTOMap, int corpListId, String xdmsHomePttId, KnPersisterTxn persisterTxn,int corpId) throws KnCorpBOException {
        final String methodName = "getSubsMappedSublistList(KnIPCorpContactDTO, int, String, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : ");
        ICorpXdmDAO corpXdmDao = new KnCorpXdmDAO(xdmsHomePttId);
        return corpXdmDao.getSubsMappedSublistList(contactDTOMap, corpListId, persisterTxn,corpId);
    }
}
