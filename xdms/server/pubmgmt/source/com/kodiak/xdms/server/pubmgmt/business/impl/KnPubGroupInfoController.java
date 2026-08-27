/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business.impl;

import com.kodiak.common.dao.KnDAOException;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnSystemException;
import com.kodiak.common.resources.KnConstants.MICROSERVICES_COMMON_CONFIG;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.logger.KnLogger;
import com.kodiak.utilities.xmlmodifier.KnXMLModifierException;
import com.kodiak.utilities.xmlmodifier.impl.KnXMLProcessor;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.business.helper.KnGenInfoUtil;
import com.kodiak.xdms.server.common.dao.KnFactorySelector;
import com.kodiak.xdms.server.common.dao.persister.IXDMServerDAO;
import com.kodiak.xdms.server.common.dao.persister.db.KnDBConnectionException;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnSubsProfileDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubsProfilePersistDTO;
import com.kodiak.xdms.server.common.dto.persistdat.KnSubscriberPersistDTO;
import com.kodiak.xdms.server.common.framework.KnFWException;
import com.kodiak.xdms.server.common.framework.aas.KnAASException;
import com.kodiak.xdms.server.common.framework.aas.KnAASFramework;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.common.framework.validator.KnValidatorFramework;
import com.kodiak.xdms.server.common.resources.KnConstants;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.common.util.KnGeneralUtil;
import com.kodiak.xdms.server.pubmgmt.business.IPubGroupInfoController;
import com.kodiak.xdms.server.pubmgmt.business.KnPubBOException;
import com.kodiak.xdms.server.pubmgmt.business.helper.KnPubInfoUtil;
import com.kodiak.xdms.server.pubmgmt.dao.KnPubFactorySelector;
import com.kodiak.xdms.server.pubmgmt.dao.persister.IPubXdmDAO;
import com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.KnPubDBTablesRegistry;
import com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm.KnPOCGroupDocMapDAO;
import com.kodiak.xdms.server.pubmgmt.dao.persister.db.tables.xdm.KnPOCGroupMemberDAO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.pubmgmt.dto.common.*;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubGroupDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnDynamicGroupPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupInfoPersistDTO;
import com.kodiak.xdms.server.pubmgmt.dto.persistdat.KnPubGroupPersistDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.pubmgmt.resources.KnErrorCodes;
import com.kodiak.xdms.server.pubmgmt.resources.KnOperationTypes;

import java.io.InputStream;
import java.util.*;

import static com.kodiak.common.resources.KnConstants.*;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubGroupInfoController.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 11, 2011           7.0
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
public class KnPubGroupInfoController implements IPubGroupInfoController {
	private static final KnLogger knLogger = KnLogger.getLogger(KnPubGroupInfoController.class);


    private static final String CLASSNAME = KnPubGroupInfoController.class.getName();
    private KnValidatorFramework validatorFwk = null;
    private KnAASFramework authorizationFwk = null;
    private KnPubInfoUtil pubInfoUtil = new KnPubInfoUtil();
    private static KnGenInfoUtil genInfoUtil = KnGenInfoUtil.getInstance();
    private static KnXMLProcessor xmlProcessor = KnXMLProcessor.getInstance();
    private KnGeneralUtil generalUtil = new KnGeneralUtil();
    int clusterId = 0;


    /**
     *
     */
    public KnPubGroupInfoController() {//throws KnFWException {
        validatorFwk = KnValidatorFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        authorizationFwk = KnAASFramework.getInstance(KnConstants.LIBRARY_NAME_PUB_MGMT);
        clusterId = Integer.parseInt(System.getenv(CLUSTERID_ENV_NAME));
    }


    /**
     * @param ipGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws com.kodiak.xdms.server.common.KnXDMServerException
     *
     * @throws com.kodiak.xdms.server.common.framework.KnFWException
     *
     */
    public KnOpPubResponse createGroup(KnIPPubGroupInfoDTO ipGroupInfoDTO,
                                       KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException {

        final String methodName = "createGroup(KnIPPubGroupInfoDTO, persisterTxn)";
        KnOpPubResponse result = new KnOpPubResponse();
        boolean readOnly = true;
        boolean isReplace = false;
        KnPubGroupInfoPersistDTO groupPersistDTO = null;
        knLogger.debug( methodName, "ENTRY -> Input DTO Passed : " + ipGroupInfoDTO);

        try {

            String ownerMdn = ipGroupInfoDTO.getOwner();
            String groupName= ipGroupInfoDTO.getGroupName();
            knLogger.debug( methodName, "The MCPTTID is create" + KnGDPRTemplate.mcpttId(ipGroupInfoDTO.getMcPttId()));
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipGroupInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
            
            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String allowPublicGroupMgmtFlag = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.ALLOW_PUBLIC_GRP_MGMT.value());

            String xdmServerId = subsProfile.getXdmsHome();
            //String pocServerId = subsProfile.getPocHome();

            groupPersistDTO = new KnPubGroupInfoPersistDTO();
            groupPersistDTO.setInputDTO(ipGroupInfoDTO);

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setInputDTO(ipGroupInfoDTO);
            originator.setMdn(ownerMdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setCorpSubscriptionType(subsProfile.getCorpSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            originator.setMcpttID(subsProfile.getMcpttId());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            groupPersistDTO.setPersistenceDTO(originator);
            groupPersistDTO.setAcrtepg(allowPublicGroupMgmtFlag);

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory( KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            IXDMServerDAO commonXdmDAO = KnFactorySelector.getDAOFactory( KnFactorySelector.DB).createXDMServerDAO(xdmServerId);

            //Fetch the client type of members
            List<KnGroupMemberDTO> listMems = new ArrayList<KnGroupMemberDTO>();
            listMems = ipGroupInfoDTO.getGroupMembers();
            ArrayList<String> memberList = new ArrayList<>();
            for(KnMemberDTO mem :listMems){
                memberList.add(mem.getMemberMdn());
            }

            List<KnMemberDTO> memberDTOs = xdmServerDAO.getMembersClientType(memberList, persisterTxn);



            groupPersistDTO.setPoCMembers(memberDTOs);


            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(groupPersistDTO);
            knLogger.debug( methodName, "Authorized successfully.");

            // Populate Group persist dto with input dto
            pubInfoUtil.populateGroupInfoPersistDTO(ipGroupInfoDTO, groupPersistDTO);
            // Populate Group member(s)
            groupPersistDTO.setGroupMembers(ipGroupInfoDTO.getGroupMembers());
            groupPersistDTO.setDocSelectorURI(ipGroupInfoDTO.getDocSelectorURI());
            groupPersistDTO.setStrXml(ipGroupInfoDTO.getStrXml());
            groupPersistDTO.setGroupType(ipGroupInfoDTO.getGroupType());
            groupPersistDTO.setListServiceURI(ipGroupInfoDTO.getListServiceURI());
            groupPersistDTO.setGroupDocURI(pubInfoUtil.constructGroupXcapUri(ownerMdn, ipGroupInfoDTO.getGroupName(),subsProfile.getClientMajorVersion()));

          //  validatorFwk.validate(groupPersistDTO);
          //  knLogger.debug( methodName, "Validated successfully!");


            // get group doc details
//            String listServiceUri = groupPersistDTO.getListServiceURI();
            int groupDocId = 0;
            int groupDocEtag = 0;
            int groupDocEtag_Updated = 1;
            KnPubGroupInfoPersistDTO grpDTO = null;
            try {
                //grpDTO = new KnPubGroupInfoPersistDTO();
                grpDTO = xdmServerDAO.getOMAGroupDocForGroupName(ownerMdn, groupPersistDTO.getListServiceURI(),
                        groupPersistDTO.getGroupDisplayName(), persisterTxn);
                if (grpDTO != null) {
	                groupDocId = grpDTO.getGroupDocId();
	                groupPersistDTO.setGroupDocId(groupDocId);
	                groupDocEtag = grpDTO.getGroupDocEtag();
	                groupDocEtag_Updated = groupDocEtag + 1;
	                if (groupDocId > 0) {
	                    isReplace = true;
	                   result.setReplaceGrp(true);
	                }
                }
            } catch(KnDAOException ex) {
                if (!KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                    throw ex;
                }
            }
            // create OMA Group doc
            if (isReplace) {
                knLogger.debug( methodName, "entering replace OMAGroup Doc");
                    ipGroupInfoDTO.setEntityId(KnEntityTypes.GROUP_MANAGER);
                    ipGroupInfoDTO.setOperationType(KnOperationTypes.MODIFY_GROUP);
                    ipGroupInfoDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
                    groupPersistDTO.setInputDTO(ipGroupInfoDTO);
                    validatorFwk.validate(groupPersistDTO);
                    knLogger.debug( methodName, "Validated successfully!");
                xdmServerDAO.replaceOMAGroupDoc(groupPersistDTO, persisterTxn);
            } else {
                    knLogger.debug( methodName, "entering create OMAGroup Doc");
                    validatorFwk.validate(groupPersistDTO);
                    knLogger.debug( methodName, "Validated successfully!");
                xdmServerDAO.createOMAGroupDoc(groupPersistDTO, persisterTxn);
            }


            // get current etag for dir
//            int docEtag = commonXdmDAO.getCurrentEtagForDirDoc(ownerMdn, persisterTxn);
            // update etag of directory
            int dirEtag = commonXdmDAO.getCurrentDirEtagForUpdate(ownerMdn, persisterTxn);
            commonXdmDAO.updateEtagForDirDoc(ownerMdn, persisterTxn);
            int updatedDirEtag = dirEtag + 1;
            Collection<KnGroupMemberDTO> existingGroupMember = null;
            if (isReplace) {
                int pocGroupId = 0;
                // getpocGroupid from groupdoc
                Collection<Integer> groupDocIds = new ArrayList<Integer>();
                groupDocIds.add(groupDocId);
                Map<Integer, Integer> groupDocVsGroupIdMap = xdmServerDAO.getPocGroupIdsForGroupDocIds(groupDocIds, readOnly, persisterTxn);
                pocGroupId = groupDocVsGroupIdMap.get(groupDocId);
                existingGroupMember = xdmServerDAO.getPocGroupMembers(pocGroupId, persisterTxn);
                xdmServerDAO.deleteAllPocGroupMembers(pocGroupId, persisterTxn);
                groupPersistDTO.setGroupId(pocGroupId);
            } else {
                // create PocGroupDocMap
                xdmServerDAO.createPocGroupDocMap(groupPersistDTO, persisterTxn);
                // create pocGroup
                xdmServerDAO.createPOCGroup(groupPersistDTO, persisterTxn);
            }

            // add PocGroupMembers
            xdmServerDAO.addPocGroupMembers(groupPersistDTO.getGroupId(), ipGroupInfoDTO.getGroupMembers(), persisterTxn);

            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
            docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.ADD.value());
            docChgDTO.setDocUri(pubInfoUtil.getXcapDirGroupFolder());
            docChgDTO.setEntryUri(pubInfoUtil.constructAddGroupEntryUri(ownerMdn, groupName));
//            if (isReplace) {
                docChgDTO.setNewEtag(String.valueOf(groupDocEtag_Updated));
//            } else {
//                docChgDTO.setNewEtag(String.valueOf(groupDocEtag_Updated));
//            }
            Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
            docChgList.add(docChgDTO);
            dirChgDTO.setDocChgDTO(docChgList);
            String dirDocUri = genInfoUtil.generateDirDocUri(ownerMdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag(String.valueOf(dirEtag));
            dirChgDTO.setDirNewEtag(String.valueOf(updatedDirEtag));
            dirChgDTO.setPocHome(subsProfile.getPocHome());
            dirChgDTO.setPresenceHome(subsProfile.getPresenceHome());

            result.setDirChgDTO(dirChgDTO);
            result.setDocEtag(String.valueOf(groupDocEtag_Updated));
            KnPubNotifyDetailsDTO pubNotifyDetailsDTO = new KnPubNotifyDetailsDTO();
            pubNotifyDetailsDTO.setAddedMembers(ipGroupInfoDTO.getGroupMembers());
            pubNotifyDetailsDTO.setOwnerMdn(ownerMdn);
            pubNotifyDetailsDTO.setGrpName(groupName);
            pubNotifyDetailsDTO.setGrpId(groupPersistDTO.getGroupId());
            pubNotifyDetailsDTO.setDocNewEtag(String.valueOf(groupDocEtag_Updated));
            if (isReplace) {
                pubNotifyDetailsDTO.setDocOldEtag(String.valueOf(groupDocEtag));
                pubNotifyDetailsDTO.setGroupState(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
                List<String> remMems = null;
                if (existingGroupMember != null) {
                    remMems = new ArrayList<>();
                    for (KnGroupMemberDTO memberDTO : existingGroupMember) {
                        if (!memberList.contains(memberDTO.getMemberMdn())) {
                            remMems.add(memberDTO.getMemberMdn());
                        }
                    }
                    pubNotifyDetailsDTO.setRemovedMembers(remMems);
                }
            } else {
                pubNotifyDetailsDTO.setGroupState(KnConstants.DOC_CHANGE_TYPE.ADD.value());
            }
            pubNotifyDetailsDTO.setClientType(subsProfile.getClientType());
            result.setPubNotifyDetailsDTO(pubNotifyDetailsDTO);

        } catch (KnAASException aex) {
            knLogger.error( methodName, "Authorization Exception occured :" + aex);
            throw aex;
        } catch (KnPubBOException ex) {
            knLogger.error( methodName, "PubBO Exception occured : " + ex);
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occured : " + ex);
            if (KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_ALREADY_EXISTS, "Group already exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occured : ", ex);
        } catch (KnValidationException vex) {
            knLogger.error( methodName, "Validation Exception occured :" + vex);

            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error( methodName, "Exception occured while Create Group: " +
                    ex.getMessage());

            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Creating Group", ex);
        }
        return result;
    }

    /**
     * @param ipGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse addGroupMember(KnIPPubGroupInfoDTO ipGroupInfoDTO,
                                          KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException {

        final String methodName = "addGroupMember(KnIPPubGroupInfoDTO, persisterTxn)";
        KnOpPubResponse result = null;
        boolean ownedTxn = false;
        boolean readOnly = false;
        KnPubGroupInfoPersistDTO groupPersistDTO = null;
        knLogger.debug( methodName, "ENTRY -> Input DTO Passed : " + ipGroupInfoDTO);
        knLogger.debug( methodName, "ENTRY -> mcpttid : " + KnGDPRTemplate.mcpttId(ipGroupInfoDTO.getMcPttId()));

        try {
            String ownerMdn = ipGroupInfoDTO.getOwner();
            String groupName= ipGroupInfoDTO.getGroupName();
            int groupDocEtag = ipGroupInfoDTO.getIfMatch();

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipGroupInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);

            String xcapDocUri = pubInfoUtil.constructGroupXcapUri(ownerMdn, groupName,subsProfile.getClientMajorVersion());
            
            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String allowPublicGroupMgmtFlag = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.ALLOW_PUBLIC_GRP_MGMT.value());

            String xdmServerId = subsProfile.getXdmsHome();
         //   String pocServerId = subsProfile.getPocHome();

            groupPersistDTO = new KnPubGroupInfoPersistDTO();
            groupPersistDTO.setInputDTO(ipGroupInfoDTO);

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setInputDTO(ipGroupInfoDTO);
            originator.setMdn(ownerMdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setCorpSubscriptionType(subsProfile.getCorpSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            originator.setMcpttID(subsProfile.getMcpttId());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            groupPersistDTO.setPersistenceDTO(originator);
            groupPersistDTO.setAcrtepg(allowPublicGroupMgmtFlag);

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory( KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            IXDMServerDAO commonXdmDAO = KnFactorySelector.getDAOFactory( KnFactorySelector.DB).createXDMServerDAO(xdmServerId);

            //Fetch the client type of members
            List<KnGroupMemberDTO> listMems = new ArrayList<KnGroupMemberDTO>();
            listMems = ipGroupInfoDTO.getGroupMembers();
            ArrayList<String> memberList1 = new ArrayList<>();
            for(KnMemberDTO mem :listMems){
                memberList1.add(mem.getMemberMdn());
            }

            List<KnMemberDTO> memberDTOs = xdmServerDAO.getMembersClientType(memberList1, persisterTxn);

            // Populate Group persist dto with input dto
            pubInfoUtil.populateGroupInfoPersistDTO(ipGroupInfoDTO, groupPersistDTO);

            groupPersistDTO.setPoCMembers(memberDTOs);

            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(groupPersistDTO);
            knLogger.debug( methodName, "Authorized successfully.");

            validatorFwk.validate(groupPersistDTO);
            knLogger.debug(methodName, "Validated successfully!");

            // Populate Group persist dto with input dto
            pubInfoUtil.populateGroupInfoPersistDTO(ipGroupInfoDTO, groupPersistDTO);
            // Populate Group member(s)
            groupPersistDTO.setGroupMembers(ipGroupInfoDTO.getGroupMembers());
            groupPersistDTO.setGroupDocURI(xcapDocUri);
            Collection<KnGroupMemberDTO> memberList = ipGroupInfoDTO.getGroupMembers();
            groupPersistDTO.setStrXml(ipGroupInfoDTO.getStrXml());
            String memberMdn = null;
            String memDisplayName = null;
            for (KnGroupMemberDTO member: memberList) {
                // only one member to delete, so break once read
                memberMdn = member.getMemberMdn();
                memDisplayName = member.getMemberName();
                break;
            }


            // get current etag for Group doc
            KnPubGroupInfoPersistDTO grpInfoPersistDTO = new KnPubGroupInfoPersistDTO();
            grpInfoPersistDTO.setOwner(ownerMdn);
            xdmServerDAO.getCurrentOMAGroupDoc(grpInfoPersistDTO, xcapDocUri, persisterTxn);

            // Get OMA Group doc Id from the xcap Doc Uri
            int groupDocId = xdmServerDAO.getGroupDocId(xcapDocUri, persisterTxn);

            // getpocGroupid from groupdoc
            Collection<Integer> groupDocIds = new ArrayList<Integer>();
            groupDocIds.add(groupDocId);
            Map<Integer, Integer> groupDocVsGroupIdMap = xdmServerDAO.getPocGroupIdsForGroupDocIds(groupDocIds, readOnly, persisterTxn);
            int pocGroupId = groupDocVsGroupIdMap.get(groupDocId);

            int grpMemCount = xdmServerDAO.getGroupMemberCount(pocGroupId, readOnly, persisterTxn);
            groupPersistDTO.setGroupMemberCount(grpMemCount);

            validatorFwk.validate(groupPersistDTO);
            knLogger.debug( methodName, "Validated successfully!");

//            int etag = xdmServerDAO.getCurrentOMAGroupDocEtag(groupPersistDTO, persisterTxn);
            int etag = grpInfoPersistDTO.getGroupDocEtag();
            if (groupDocEtag > 0 && etag != groupDocEtag) {
                knLogger.error( methodName, "Etag mismatch while adding Group member : " + etag);
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_MODIFIED, "Group modified");
            }

            // check if member being added already exists in the group
            if (xdmServerDAO.checkGroupMemberExists(pocGroupId, memberMdn, readOnly, persisterTxn)) {
                knLogger.error( methodName, "Mem Already Exists in the Group :" , KnGDPRTemplate.mdn(memberMdn));
                throw new KnPubBOException(KnErrorCodes.BOEntity.GROUP_MEMBER_ALREADY_EXISTS,
                        "Group member already exists");
            }

            //get the current xmlDoc in the DB
//            InputStream xmlDoc = xdmServerDAO.getCurrentOMAGroupDocXmlDoc(ownerMdn, xcapDocUri, 0,
//                    readOnly, persisterTxn);
            InputStream xmlDoc = grpInfoPersistDTO.getXmlDoc();
            knLogger.debug( methodName, "Fetched the existing XML Doc from DB : " , xmlDoc);

            // delete group member in  current doc using xml parser
            //InputStream updatedXmlDoc = xmlProcessor.addElement(xmlDoc,
            //        pubInfoUtil.constructGroupMemberEntryXPath(null, null), groupPersistDTO.getStrXml());

            // JDOM
            InputStream updatedXmlDoc = xmlProcessor.addMemberMdn(xmlDoc, memberMdn, memDisplayName);

            knLogger.debug( methodName, "After adding the fragment : " + updatedXmlDoc);
            groupPersistDTO.setXmlDoc(updatedXmlDoc);

            // get current etag for dir
//            int dirEtag = commonXdmDAO.getCurrentEtagForDirDoc(ownerMdn, persisterTxn);

            // update xmldoc
            xdmServerDAO.updateOMAGroupDocXmlDoc(groupPersistDTO, persisterTxn);
            int updatedGroupDocEtag = etag + 1;

            // update etag of directory
            int dirEtag = commonXdmDAO.getCurrentDirEtagForUpdate(ownerMdn, persisterTxn);
            commonXdmDAO.updateEtagForDirDoc(ownerMdn, persisterTxn);
            int updatedDirEtag = dirEtag + 1;

            // add member
            xdmServerDAO.addPocGroupMembers(pocGroupId, ipGroupInfoDTO.getGroupMembers(), persisterTxn);
            // update etag in pocgroup docmap with groupdoc etag
            xdmServerDAO.updateEtagForPOCGroupDocMap(groupDocId, updatedGroupDocEtag, persisterTxn);

            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
            docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            String groupDocUri = pubInfoUtil.constructGroupSelUri(ownerMdn, groupName);
            docChgDTO.setDocUri(groupDocUri);
            docChgDTO.setNewEtag(String.valueOf(updatedGroupDocEtag));
            Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
            docChgList.add(docChgDTO);
            dirChgDTO.setDocChgDTO(docChgList);
            String dirDocUri = genInfoUtil.generateDirDocUri(ownerMdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag(String.valueOf(dirEtag));
            dirChgDTO.setDirNewEtag(String.valueOf(updatedDirEtag));
            dirChgDTO.setPocHome(subsProfile.getPocHome());
            dirChgDTO.setPresenceHome(subsProfile.getPresenceHome());

            result = pubInfoUtil.populateSuccessResponse();
            result.setDirChgDTO(dirChgDTO);
            result.setDocEtag(String.valueOf(updatedGroupDocEtag));
            KnPubNotifyDetailsDTO pubNotifyDetailsDTO = new KnPubNotifyDetailsDTO();
            pubNotifyDetailsDTO.setOwnerMdn(ownerMdn);
            pubNotifyDetailsDTO.setClientType(subsProfile.getClientType());
            pubNotifyDetailsDTO.setAddedMembers(ipGroupInfoDTO.getGroupMembers());
            pubNotifyDetailsDTO.setGrpId(pocGroupId);
            pubNotifyDetailsDTO.setGrpName(groupName);
            pubNotifyDetailsDTO.setDocNewEtag(String.valueOf(updatedGroupDocEtag));
            pubNotifyDetailsDTO.setDocOldEtag(String.valueOf(etag));
            pubNotifyDetailsDTO.setGroupState(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            result.setPubNotifyDetailsDTO(pubNotifyDetailsDTO);

        } catch (KnAASException aex) {
            knLogger.error( methodName, "Authorization Exception occured :" , aex);

            throw aex;
        }  catch (KnXMLModifierException xme) {
            knLogger.error( methodName, "Exception while modifying the XML :" , xme);

            throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_MEMBER_DOES_NOT_EXISTS,
                    "Group member Does not exists");
        } catch (KnPubBOException ex) {
            knLogger.error( methodName, "PubBO Exception occured : " + ex);

            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occured : " , ex);

            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_DOES_NOT_EXISTS,
                        "Group does not exists");
            } else if (KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_MEMBER_ALREADY_EXISTS,
                        "Group Member already exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occured :" , ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occured : ", ex);
        } catch (KnValidationException vex) {
            knLogger.error( methodName, "Validation Exception occured :" , vex);

            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error( methodName, "Exception occured while adding member to Group: " +
                    ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "adding member to Group", ex);
        }
        return result;
    }

    /**
     * @param ipGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse modifyGroupMember(KnIPPubGroupInfoDTO ipGroupInfoDTO,
                                             KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException {

        String methodName = "modifyGroupMember(KnIPPubGroupInfoDTO, persisterTxn)";
        KnOpPubResponse result = null;
        boolean ownedTxn = false;
        boolean readOnly = false;
        KnPubGroupInfoPersistDTO groupPersistDTO = null;
        knLogger.debug( methodName, "ENTRY -> Input DTO Passed : " + ipGroupInfoDTO);
        knLogger.debug(methodName, "MCPTTID: "+KnGDPRTemplate.mcpttId(ipGroupInfoDTO.getMcPttId()));

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            String ownerMdn = ipGroupInfoDTO.getOwner();
            String groupName= ipGroupInfoDTO.getGroupName();
            int groupDocEtag = ipGroupInfoDTO.getIfMatch();

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipGroupInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);

            String xcapDocUri = pubInfoUtil.constructGroupXcapUri(ownerMdn, groupName,subsProfile.getClientMajorVersion());
            
            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String allowPublicGroupMgmtFlag = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.ALLOW_PUBLIC_GRP_MGMT.value());

            String xdmServerId = subsProfile.getXdmsHome();
         //   String pocServerId = subsProfile.getPocHome();

            groupPersistDTO = new KnPubGroupInfoPersistDTO();
            groupPersistDTO.setInputDTO(ipGroupInfoDTO);

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setInputDTO(ipGroupInfoDTO);
            originator.setMdn(ownerMdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setCorpSubscriptionType(subsProfile.getCorpSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            originator.setMcpttID(subsProfile.getMcpttId());
            groupPersistDTO.setPersistenceDTO(originator);
            groupPersistDTO.setAcrtepg(allowPublicGroupMgmtFlag);

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory( KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            IXDMServerDAO commonXdmDAO = KnFactorySelector.getDAOFactory( KnFactorySelector.DB).createXDMServerDAO(xdmServerId);

            //Fetch the client type of members
            List<KnGroupMemberDTO> listMems = new ArrayList<KnGroupMemberDTO>();
            listMems = ipGroupInfoDTO.getGroupMembers();
            ArrayList<String> memberList = new ArrayList<>();
            for(KnMemberDTO mem :listMems){
                memberList.add(mem.getMemberMdn());
            }

            List<KnMemberDTO> memberDTOs = xdmServerDAO.getMembersClientType(memberList, persisterTxn);



            groupPersistDTO.setPoCMembers(memberDTOs);

            knLogger.debug(methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(groupPersistDTO);
            knLogger.debug( methodName, "Authorized successfully.");

            // Populate Group persist dto with input dto
            pubInfoUtil.populateGroupInfoPersistDTO(ipGroupInfoDTO, groupPersistDTO);
            // Populate Group member(s)
            groupPersistDTO.setGroupMembers(ipGroupInfoDTO.getGroupMembers());
            groupPersistDTO.setGroupDocURI(xcapDocUri);
            groupPersistDTO.setStrXml(ipGroupInfoDTO.getStrXml());

            validatorFwk.validate(groupPersistDTO);
            knLogger.debug( methodName, "Validated successfully!");



            // get current etag for index doc
            KnPubGroupInfoPersistDTO grpInfoPersistDTO = new KnPubGroupInfoPersistDTO();
            grpInfoPersistDTO.setOwner(ownerMdn);
            xdmServerDAO.getCurrentOMAGroupDoc(grpInfoPersistDTO, xcapDocUri, persisterTxn);
//            int etag = xdmServerDAO.getCurrentOMAGroupDocEtag(groupPersistDTO, persisterTxn);
            int etag = grpInfoPersistDTO.getGroupDocEtag();
            if (groupDocEtag > 0 && etag != groupDocEtag) {
                knLogger.error( methodName, "Etag mismatch while Delete Group member : " + etag);
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_MODIFIED, "Group modified");
            }

            // Get OMA Group doc Id from the xcap Doc Uri
            int groupDocId = xdmServerDAO.getGroupDocId(xcapDocUri, persisterTxn);

            //get the current xmlDoc in the DB
//            InputStream xmlDoc = xdmServerDAO.getCurrentOMAGroupDocXmlDoc(ownerMdn, xcapDocUri, 0,
//                    readOnly, persisterTxn);
            InputStream xmlDoc = grpInfoPersistDTO.getXmlDoc();
            knLogger.debug( methodName, "Fetched the existing XML Doc from DB : " + xmlDoc);

            Collection<KnGroupMemberDTO> members = groupPersistDTO.getGroupMembers();
            String memberMdn = null;
            String modMemName = null;
            for (KnGroupMemberDTO member : members) {
                memberMdn = member.getMemberMdn();
                modMemName = member.getMemberName();
            }
            knLogger.debug( methodName, "Member whose name is being modified : " + KnGDPRTemplate.mdn(memberMdn));
            knLogger.debug( methodName, "Modified member name : " + KnGDPRTemplate.name(modMemName));

            Collection<KnMemberDetailsDTO> memberDetails = ipGroupInfoDTO.getMemberDetails();
            String uriMdn = null;
            if (memberDetails != null && !memberDetails.isEmpty()) {
                for (KnMemberDetailsDTO memDetail: memberDetails) {
                    uriMdn = memDetail.getUri();
                    break;
                }
            }
            knLogger.debug( methodName, "URI Member : " + KnGDPRTemplate.mdn(uriMdn));

            InputStream updatedXmlDoc = xmlProcessor.updateMemberMdn(xmlDoc, uriMdn, modMemName);
            knLogger.debug( methodName, "After deleting the fragment : " + updatedXmlDoc);
            groupPersistDTO.setXmlDoc(updatedXmlDoc);

            // update xmldoc
            xdmServerDAO.updateOMAGroupDocXmlDoc(groupPersistDTO, persisterTxn);
            int updatedGroupDocEtag = etag + 1;

            Collection<Integer> groupdocIds = new ArrayList<Integer>();
            groupdocIds.add(groupDocId);
            Map<Integer, Integer> pocGroupIds = xdmServerDAO.getPocGroupIdsForGroupDocIds(groupdocIds, readOnly, persisterTxn);
            int pocGroupId = pocGroupIds.get(groupDocId);

            // Update Directory Etag


            // get current etag for dir
//            int dirEtag = commonXdmDAO.getCurrentEtagForDirDoc(ownerMdn, persisterTxn);
            // update etag of directory
            int dirEtag = commonXdmDAO.getCurrentDirEtagForUpdate(ownerMdn, persisterTxn);
            commonXdmDAO.updateEtagForDirDoc(ownerMdn, persisterTxn);
            int updatedDirEtag = dirEtag + 1;

            // update etag in pocgroup docmap with groupdoc etag
            xdmServerDAO.updateEtagForPOCGroupDocMap(groupDocId, updatedGroupDocEtag, persisterTxn);

            // Update poc group member name(s)
            xdmServerDAO.modifyPocGroupMembers(pocGroupId, ipGroupInfoDTO.getGroupMembers(), persisterTxn);

            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
            docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            String contactDocUri = pubInfoUtil.constructGroupSelUri(ownerMdn, groupName);
            docChgDTO.setDocUri(contactDocUri);
            docChgDTO.setNewEtag(String.valueOf(updatedGroupDocEtag));
            Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
            docChgList.add(docChgDTO);
            dirChgDTO.setDocChgDTO(docChgList);
            String dirDocUri = genInfoUtil.generateDirDocUri(ownerMdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag(String.valueOf(dirEtag));
            dirChgDTO.setDirNewEtag(String.valueOf(updatedDirEtag));
            dirChgDTO.setPocHome(subsProfile.getPocHome());
            dirChgDTO.setPresenceHome(subsProfile.getPresenceHome());

            result = pubInfoUtil.populateSuccessResponse();
            result.setDirChgDTO(dirChgDTO);
            result.setDocEtag(String.valueOf(updatedGroupDocEtag));
            KnPubNotifyDetailsDTO pubNotifyDetailsDTO = new KnPubNotifyDetailsDTO();
            pubNotifyDetailsDTO.setModifiedMembers(ipGroupInfoDTO.getGroupMembers());
            pubNotifyDetailsDTO.setOwnerMdn(ownerMdn);
            pubNotifyDetailsDTO.setGrpName(groupName);
            pubNotifyDetailsDTO.setGrpId(pocGroupId);
            pubNotifyDetailsDTO.setDocNewEtag(String.valueOf(updatedGroupDocEtag));
            pubNotifyDetailsDTO.setGroupState(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            pubNotifyDetailsDTO.setClientType(subsProfile.getClientType());
            result.setPubNotifyDetailsDTO(pubNotifyDetailsDTO);
            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (KnAASException aex) {
            knLogger.error( methodName, "Authorization Exception occured :" + aex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw aex;
        }  catch (KnXMLModifierException xme) {
            knLogger.error( methodName, "Exception while modifying the XML :" + xme);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_MEMBER_DOES_NOT_EXISTS,
                    "Group member Does not exists");
        } catch (KnPubBOException ex) {
            knLogger.error( methodName, "PubBO Exception occured : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occured : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_MEMBER_DOES_NOT_EXISTS,
                        "Group Member doesn't exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);
        } catch (KnValidationException vex) {
            knLogger.error( methodName, "Validation Exception occured :" + vex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error( methodName, "Exception occured while updating Group Member Name: " +
                    ex.getMessage());
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Updating Group Member name", ex);
        }
        return result;
    }

    /**
     * @param ipGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse modifyGroupName(KnIPPubGroupInfoDTO ipGroupInfoDTO,
                                           KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException {

        String methodName = "modifyGroupName(KnIPPubGroupInfoDTO, persisterTxn)";
        KnOpPubResponse result = null;
        boolean ownedTxn = false;
        boolean readOnly = true;
        KnPubGroupInfoPersistDTO groupPersistDTO = null;
        knLogger.debug( methodName, "ENTRY -> Input DTO Passed : " + ipGroupInfoDTO);
        knLogger.debug(methodName, "MCPTTID: "+KnGDPRTemplate.mcpttId(ipGroupInfoDTO.getMcPttId()));

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            String ownerMdn = ipGroupInfoDTO.getOwner();
            String groupName= ipGroupInfoDTO.getGroupName();
            int groupDocEtag = ipGroupInfoDTO.getIfMatch();

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipGroupInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);

            String xcapDocUri = pubInfoUtil.constructGroupXcapUri(ownerMdn, groupName,subsProfile.getClientMajorVersion());
            
            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String allowPublicGroupMgmtFlag = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.ALLOW_PUBLIC_GRP_MGMT.value());

            String xdmServerId = subsProfile.getXdmsHome();
         //   String pocServerId = subsProfile.getPocHome();

            groupPersistDTO = new KnPubGroupInfoPersistDTO();
            groupPersistDTO.setInputDTO(ipGroupInfoDTO);

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setInputDTO(ipGroupInfoDTO);
            originator.setMdn(ownerMdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            originator.setCorpSubscriptionType(subsProfile.getCorpSubscriptionType());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            originator.setMcpttID(subsProfile.getMcpttId());
            groupPersistDTO.setPersistenceDTO(originator);
            groupPersistDTO.setAcrtepg(allowPublicGroupMgmtFlag);

            knLogger.debug( methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(groupPersistDTO);
            knLogger.debug( methodName, "Authorized successfully.");

            // Populate Group persist dto with input dto
            pubInfoUtil.populateGroupInfoPersistDTO(ipGroupInfoDTO, groupPersistDTO);
            groupPersistDTO.setGroupDocURI(xcapDocUri);
            groupPersistDTO.setStrXml(ipGroupInfoDTO.getStrXml());
            groupPersistDTO.setGroupDisplayName(ipGroupInfoDTO.getGroupDisplayName());

            validatorFwk.validate(groupPersistDTO);
            knLogger.debug( methodName, "Validated successfully!");

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                                            KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            // get current etag for index doc
            KnPubGroupInfoPersistDTO grpInfoPersistDTO = new KnPubGroupInfoPersistDTO();
            grpInfoPersistDTO.setOwner(ownerMdn);
            xdmServerDAO.getCurrentOMAGroupDoc(grpInfoPersistDTO, xcapDocUri, persisterTxn);
//            int etag = xdmServerDAO.getCurrentOMAGroupDocEtag(groupPersistDTO, persisterTxn);
            int etag = grpInfoPersistDTO.getGroupDocEtag();
            if (groupDocEtag > 0 && etag != groupDocEtag) {
                knLogger.error( methodName, "Etag mismatch while Delete Group member : " + etag);
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_MODIFIED, "Group modified");
            }

            //get the current xmlDoc in the DB
//            InputStream xmlDoc = xdmServerDAO.getCurrentOMAGroupDocXmlDoc(ownerMdn, xcapDocUri, 0,
//                    readOnly, persisterTxn);
            InputStream xmlDoc = grpInfoPersistDTO.getXmlDoc();
            knLogger.debug( methodName, "Fetched the existing XML Doc from DB : " + xmlDoc);

            InputStream updatedXmlDoc = xmlProcessor.updateGroupDisplayName(xmlDoc, groupPersistDTO.getGroupDisplayName());
            knLogger.debug( methodName, "After deleting the fragment : " + updatedXmlDoc);
            groupPersistDTO.setXmlDoc(updatedXmlDoc);

            // update xmldoc
            xdmServerDAO.updateOMAGroupDocXmlDoc(groupPersistDTO, persisterTxn);
            int updatedGroupDocEtag = etag + 1;

            // update group display name
            xdmServerDAO.updateGroupDisplayName(groupPersistDTO, persisterTxn);

            // Update Directory Etag
            IXDMServerDAO commonXdmDAO = KnFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXDMServerDAO(xdmServerId);

            // get current etag for dir
//            int dirEtag = commonXdmDAO.getCurrentEtagForDirDoc(ownerMdn, persisterTxn);
            // update etag of directory
            int dirEtag = commonXdmDAO.getCurrentDirEtagForUpdate(ownerMdn, persisterTxn);
            commonXdmDAO.updateEtagForDirDoc(ownerMdn, persisterTxn);
            int updatedDirEtag = dirEtag + 1;

            // Get OMA Group doc Id from the xcap Doc Uri
            int groupDocId = xdmServerDAO.getGroupDocId(xcapDocUri, persisterTxn);

            // getpocGroupid from groupdoc
            Collection<Integer> groupDocIds = new ArrayList<Integer>();
            groupDocIds.add(groupDocId);
            Map<Integer, Integer> groupDocVsGroupIdMap = xdmServerDAO.getPocGroupIdsForGroupDocIds(
                    groupDocIds, readOnly, persisterTxn);
            //int pocGroupId = groupDocVsGroupIdMap.get(groupDocId);

            //TODO update group name relational after membername column is added

            // update etag in pocgroup docmap with groupdoc etag
            xdmServerDAO.updateEtagForPOCGroupDocMap(groupDocId, updatedGroupDocEtag, persisterTxn);

            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
            docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            String groupDocUri = pubInfoUtil.constructGroupSelUri(ownerMdn, groupName);
            docChgDTO.setDocUri(groupDocUri);
            docChgDTO.setNewEtag(String.valueOf(updatedGroupDocEtag));
            Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
            docChgList.add(docChgDTO);
            dirChgDTO.setDocChgDTO(docChgList);
            String dirDocUri = genInfoUtil.generateDirDocUri(ownerMdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag(String.valueOf(dirEtag));
            dirChgDTO.setDirNewEtag(String.valueOf(updatedDirEtag));
            dirChgDTO.setPocHome(subsProfile.getPocHome());
            dirChgDTO.setPresenceHome(subsProfile.getPresenceHome());

            result = pubInfoUtil.populateSuccessResponse();
            result.setDirChgDTO(dirChgDTO);
            result.setDocEtag(String.valueOf(updatedGroupDocEtag));
            /*KnPubNotifyDetailsDTO pubNotifyDetailsDTO = new KnPubNotifyDetailsDTO();
            pubNotifyDetailsDTO.setOwnerMdn(ownerMdn);
            pubNotifyDetailsDTO.setGrpName(groupName);
            pubNotifyDetailsDTO.setGrpId(groupPersistDTO.getGroupId());
            pubNotifyDetailsDTO.setDocNewEtag(String.valueOf(updatedGroupDocEtag));
            pubNotifyDetailsDTO.setGroupState(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            pubNotifyDetailsDTO.setClientType(subsProfile.getClientType());
            result.setPubNotifyDetailsDTO(pubNotifyDetailsDTO);*/
            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (KnAASException aex) {
            knLogger.error( methodName, "Authorization Exception occured :" + aex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw aex;
        } catch (KnXMLModifierException xme) {
            knLogger.error( methodName, "Exception while modifying the XML :" + xme);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_MEMBER_DOES_NOT_EXISTS,
                    "Group member Does not exists");
        } catch (KnPubBOException ex) {
            knLogger.error( methodName, "PubBO Exception occured : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occured : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_DOES_NOT_EXISTS, "Old Group Doesn't exists");
            } else if (KnErrorCodes.DAO.ROW_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_NAME_ALREADY_EXISTS,
                        "New Group Name already exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occured : ", ex);
        } catch (KnValidationException vex) {
            knLogger.error( methodName, "Validation Exception occured :" + vex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw vex;
        } catch (Exception ex) {
            knLogger.error( methodName, "Exception occured while Updating Group Name: " +
                    ex.getMessage());
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Updating Group Name", ex);
        }
        return result;
    }

    /**
     * @param ipGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse deleteGroupMember(KnIPPubGroupInfoDTO ipGroupInfoDTO,
                                             KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException {

        String methodName = "deleteGroupMember(KnIPPubGroupInfoDTO, persisterTxn)";
        KnOpPubResponse result = null;
        boolean readOnly = true;
        KnPubGroupInfoPersistDTO groupPersistDTO = null;
        knLogger.debug( methodName, "ENTRY -> Input DTO Passed : " + ipGroupInfoDTO);

        try {

            String ownerMdn = ipGroupInfoDTO.getOwner();
            String groupName= ipGroupInfoDTO.getGroupName();
            int groupDocEtag = ipGroupInfoDTO.getIfMatch();

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipGroupInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);

            String xdmServerId = subsProfile.getXdmsHome();
           // String pocServerId = subsProfile.getPocHome();
            String xcapDocUri = pubInfoUtil.constructGroupXcapUri(ownerMdn, groupName,subsProfile.getClientMajorVersion());
            
            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String allowPublicGroupMgmtFlag = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.ALLOW_PUBLIC_GRP_MGMT.value());

            groupPersistDTO = new KnPubGroupInfoPersistDTO();
            groupPersistDTO.setInputDTO(ipGroupInfoDTO);

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setInputDTO(ipGroupInfoDTO);
            originator.setMcpttID(subsProfile.getMcpttId());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            originator.setMdn(ownerMdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setCorpSubscriptionType(subsProfile.getCorpSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            groupPersistDTO.setPersistenceDTO(originator);
            groupPersistDTO.setAcrtepg(allowPublicGroupMgmtFlag);

            knLogger.debug( methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(groupPersistDTO);
            knLogger.debug( methodName, "Authorized successfully.");

            // Populate Group persist dto with input dto
            pubInfoUtil.populateGroupInfoPersistDTO(ipGroupInfoDTO, groupPersistDTO);
            // Populate Group member(s)
            groupPersistDTO.setGroupMembers(ipGroupInfoDTO.getGroupMembers());
            groupPersistDTO.setGroupDocURI(xcapDocUri);
            Collection<KnGroupMemberDTO> memberList = ipGroupInfoDTO.getGroupMembers();
            String memberMdn = null;
            for (KnGroupMemberDTO member: memberList) {
                // only one member to delete, so break once read
             //   memberMdn = member.getMemberMdn();
                break;
            }

            Collection<KnMemberDetailsDTO> memberDetails = ipGroupInfoDTO.getMemberDetails();
            String uriMdn = null;
            if (memberDetails != null && !memberDetails.isEmpty()) {
                for (KnMemberDetailsDTO memDetail: memberDetails) {
                    uriMdn = memDetail.getUri();
                    break;
                }
            }
            knLogger.debug( methodName, "URI Member : " + KnGDPRTemplate.mdn(uriMdn));


            validatorFwk.validate(groupPersistDTO);
            knLogger.debug( methodName, "Validated successfully!");

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                                            KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            // get current etag for index doc
            KnPubGroupInfoPersistDTO grpInfoPersistDTO = new KnPubGroupInfoPersistDTO();
            grpInfoPersistDTO.setOwner(ownerMdn);
            xdmServerDAO.getCurrentOMAGroupDoc(grpInfoPersistDTO, xcapDocUri, persisterTxn);
//            int etag = xdmServerDAO.getCurrentOMAGroupDocEtag(groupPersistDTO, persisterTxn);
            int etag = grpInfoPersistDTO.getGroupDocEtag();
            if (groupDocEtag > 0 && etag != groupDocEtag) {
                knLogger.error( methodName, "Etag mismatch while Delete Group member : " + etag);
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_MODIFIED, "Group modified");
            }

            //get the current xmlDoc in the DB
//            InputStream xmlDoc = xdmServerDAO.getCurrentOMAGroupDocXmlDoc(ownerMdn, xcapDocUri, 0,
//                    readOnly, persisterTxn);
            InputStream xmlDoc = grpInfoPersistDTO.getXmlDoc();
            knLogger.debug( methodName, "Fetched the existing XML Doc from DB : " + xmlDoc);

            // delete group member in  current doc using xml parser
            InputStream updatedXmlDoc = xmlProcessor.deleteMemberMdn(xmlDoc, uriMdn, null);
            knLogger.debug( methodName, "After deleting the fragment : " + updatedXmlDoc);
            groupPersistDTO.setXmlDoc(updatedXmlDoc);

            // update xmldoc
            xdmServerDAO.updateOMAGroupDocXmlDoc(groupPersistDTO, persisterTxn);
            int updatedGroupDocEtag = etag + 1;

            // Update Directory Etag
            IXDMServerDAO commonXdmDAO = KnFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXDMServerDAO(xdmServerId);

            // get current etag for dir
//            int dirEtag = commonXdmDAO.getCurrentEtagForDirDoc(ownerMdn, persisterTxn);
            // update etag of directory
            int dirEtag = commonXdmDAO.getCurrentDirEtagForUpdate(ownerMdn, persisterTxn);
            commonXdmDAO.updateEtagForDirDoc(ownerMdn, persisterTxn);
            int updatedDirEtag = dirEtag + 1;

            // Get OMA Group doc Id from the xcap Doc Uri
            int groupDocId = xdmServerDAO.getGroupDocId(xcapDocUri, persisterTxn);

            // getpocGroupid from groupdoc
            Collection<Integer> groupDocIds = new ArrayList<Integer>();
            groupDocIds.add(groupDocId);
            Map<Integer, Integer> groupDocVsGroupIdMap = xdmServerDAO.getPocGroupIdsForGroupDocIds(
                    groupDocIds, readOnly, persisterTxn);
            int pocGroupId = groupDocVsGroupIdMap.get(groupDocId);

            // delete member
            Collection<String> memberMdnList = new ArrayList<String>();
            for (KnGroupMemberDTO member : memberList) {
                memberMdnList.add(member.getMemberMdn());
            }
            xdmServerDAO.deletePocGroupMembers(pocGroupId, memberMdnList, persisterTxn);

            // update etag in pocgroup docmap with groupdoc etag
            xdmServerDAO.updateEtagForPOCGroupDocMap(groupDocId, updatedGroupDocEtag, persisterTxn);

            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
            docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            String groupDocUri = pubInfoUtil.constructGroupSelUri(ownerMdn, groupName);
            docChgDTO.setDocUri(groupDocUri);
            docChgDTO.setNewEtag(String.valueOf(updatedGroupDocEtag));
            Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
            docChgList.add(docChgDTO);
            dirChgDTO.setDocChgDTO(docChgList);
            String dirDocUri = genInfoUtil.generateDirDocUri(ownerMdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag(String.valueOf(dirEtag));
            dirChgDTO.setDirNewEtag(String.valueOf(updatedDirEtag));
            dirChgDTO.setPocHome(subsProfile.getPocHome());
            dirChgDTO.setPresenceHome(subsProfile.getPresenceHome());

            result = pubInfoUtil.populateSuccessResponse();
            result.setDirChgDTO(dirChgDTO);
            result.setDocEtag(String.valueOf(updatedGroupDocEtag));
            KnPubNotifyDetailsDTO pubNotifyDetailsDTO = new KnPubNotifyDetailsDTO();
            pubNotifyDetailsDTO.setOwnerMdn(ownerMdn);
            pubNotifyDetailsDTO.setClientType(subsProfile.getClientType());
            pubNotifyDetailsDTO.setRemovedMembers(memberMdnList);
            pubNotifyDetailsDTO.setGrpId(pocGroupId);
            pubNotifyDetailsDTO.setGrpName(groupName);
            pubNotifyDetailsDTO.setDocNewEtag(String.valueOf(updatedGroupDocEtag));
            pubNotifyDetailsDTO.setDocOldEtag(String.valueOf(etag));
            pubNotifyDetailsDTO.setGroupState(KnConstants.DOC_CHANGE_TYPE.REPLACE.value());
            result.setPubNotifyDetailsDTO(pubNotifyDetailsDTO);

        } catch (KnAASException aex) {
            knLogger.error( methodName, "Authorization Exception occured :" + aex);

            throw aex;
        }  catch (KnXMLModifierException xme) {
            knLogger.error( methodName, "Exception while modifying the XML :" + xme);

            throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_MEMBER_DOES_NOT_EXISTS,
                    "Group member Does not exists");
        } catch (KnPubBOException ex) {
            knLogger.error( methodName, "PubBO Exception occured : " + ex);

            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occured : " + ex);

            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_MEMBER_DOES_NOT_EXISTS,
                        "Group Member doesn't exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);
        } catch (KnValidationException vex) {
            knLogger.error( methodName, "Validation Exception occured :" + vex);
            throw vex;
        } catch (Exception ex) {
            knLogger.error( methodName, "Exception occured while Deleting Group Member: " +
                    ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Deleting Group Member", ex);
        }
        return result;
    }

    /**
     * @param ipGroupDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse deleteGroup(KnIPPubGroupDTO ipGroupDTO,
                                       KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException {

        String methodName = "deleteGroup(KnIPPubGroupDTO, persisterTxn)";
        KnOpPubResponse result = null;
        boolean readOnly = false;
        KnPubGroupInfoPersistDTO groupPersistDTO = null;
        knLogger.debug( methodName, "ENTRY -> Input DTO Passed : " + ipGroupDTO);
        try {
            String ownerMdn = ipGroupDTO.getOwner();
            String groupName= ipGroupDTO.getGroupName();
            int groupDocEtag = ipGroupDTO.getIfMatch();

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipGroupDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
            String xcapDocUri = pubInfoUtil.constructGroupXcapUri(ownerMdn, groupName,subsProfile.getClientMajorVersion());
            
            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String allowPublicGroupMgmtFlag = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.ALLOW_PUBLIC_GRP_MGMT.value());

            String xdmServerId = subsProfile.getXdmsHome();
         //   String pocServerId = subsProfile.getPocHome();

            groupPersistDTO = new KnPubGroupInfoPersistDTO();
            groupPersistDTO.setInputDTO(ipGroupDTO);

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setInputDTO(ipGroupDTO);
            originator.setMdn(ownerMdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setCorpSubscriptionType(subsProfile.getCorpSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            originator.setMcpttID(subsProfile.getMcpttId());
            groupPersistDTO.setPersistenceDTO(originator);
            groupPersistDTO.setAcrtepg(allowPublicGroupMgmtFlag);

            knLogger.debug( methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(groupPersistDTO);
            knLogger.debug( methodName, "Authorized successfully.");

            // Populate Group persist dto with input dto
            pubInfoUtil.populateGroupInfoPersistDTO(ipGroupDTO, groupPersistDTO);
            groupPersistDTO.setGroupDocURI(xcapDocUri);

            validatorFwk.validate(groupPersistDTO);
            knLogger.debug( methodName, "Validated successfully!");

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                                            KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            // get current etag for Group doc
            int etag = xdmServerDAO.getCurrentOMAGroupDocEtag(groupPersistDTO, persisterTxn);
            if (groupDocEtag > 0 && etag != groupDocEtag) {
                knLogger.error( methodName, "Etag mismatch while deleting Group: " + etag);
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_MODIFIED, "Group modified");
            }

            // Get OMA Group doc Id from the xcap Doc Uri
            int groupDocId = xdmServerDAO.getGroupDocId(xcapDocUri, persisterTxn);
            // Delete OMA Group doc
            xdmServerDAO.deleteOMAGroupDoc(groupDocId, persisterTxn);
            // Update Directory Etag

            IXDMServerDAO commonXdmDAO = KnFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXDMServerDAO(xdmServerId);

            // get current etag for dir
//            int dirEtag = commonXdmDAO.getCurrentEtagForDirDoc(ownerMdn, persisterTxn);
            // update etag of directory
            int dirEtag = commonXdmDAO.getCurrentDirEtagForUpdate(ownerMdn, persisterTxn);
            commonXdmDAO.updateEtagForDirDoc(ownerMdn, persisterTxn);
            int updatedDirEtag = dirEtag + 1;

            // get pocGroupId for groupDocId
            Collection<Integer> groupDocIds = new ArrayList<Integer>();
            groupDocIds.add(groupDocId);
            Map<Integer, Integer> groupDocVsGroupIdMap = xdmServerDAO.getPocGroupIdsForGroupDocIds(groupDocIds, readOnly, persisterTxn);
            int pocGroupId = groupDocVsGroupIdMap.get(groupDocId);
            // Delete All PocGroupMembers
            xdmServerDAO.deleteAllPocGroupMembers(pocGroupId, persisterTxn);
            // Delete pocGroup
            xdmServerDAO.deletePocGroup(pocGroupId, persisterTxn);
            // delete PocGroupDocMap
            xdmServerDAO.deletePocGroupDocMap(pocGroupId, persisterTxn);

            KnOPDirChgDTO dirChgDTO = new KnOPDirChgDTO();
            KnOPDocChgDTO docChgDTO = new KnOPDocChgDTO();
            docChgDTO.setDocumentChgType(KnConstants.DOC_CHANGE_TYPE.REMOVE.value());
            String groupDelSelUri = pubInfoUtil.constructGroupDelUri(ownerMdn, groupName);
            docChgDTO.setDocUri(groupDelSelUri);
            docChgDTO.setNewEtag(String.valueOf(etag + 1));
            Collection<KnOPDocChgDTO> docChgList = new ArrayList<KnOPDocChgDTO>();
            docChgList.add(docChgDTO);
            dirChgDTO.setDocChgDTO(docChgList);
            String dirDocUri = genInfoUtil.generateDirDocUri(ownerMdn);
            dirChgDTO.setDirUri(dirDocUri);
            dirChgDTO.setDirPrevEtag(String.valueOf(dirEtag));
            dirChgDTO.setDirNewEtag(String.valueOf(updatedDirEtag));
            dirChgDTO.setPocHome(subsProfile.getPocHome());
            dirChgDTO.setPresenceHome(subsProfile.getPresenceHome());

            result = pubInfoUtil.populateSuccessResponse();
            result.setDirChgDTO(dirChgDTO);
            KnPubNotifyDetailsDTO pubNotifyDetailsDTO = new KnPubNotifyDetailsDTO();
            pubNotifyDetailsDTO.setGrpId(pocGroupId);
            pubNotifyDetailsDTO.setOwnerMdn(ownerMdn);
            pubNotifyDetailsDTO.setClientType(subsProfile.getClientType());
            pubNotifyDetailsDTO.setGrpName(groupName);
            pubNotifyDetailsDTO.setGroupState(KnConstants.DOC_CHANGE_TYPE.REMOVE.value());
            result.setPubNotifyDetailsDTO(pubNotifyDetailsDTO);
//            result.setDocEtag(String.valueOf(etag + 1));

        } catch (KnAASException aex) {
            knLogger.error( methodName, "Authorization Exception occured :" + aex);

            throw aex;
        } catch (KnPubBOException ex) {
            knLogger.error( methodName, "PubBO Exception occured : " + ex);

            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occured : " + ex);

            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_DOES_NOT_EXISTS, "Group doesn't exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);
        } catch (KnValidationException vex) {
            knLogger.error( methodName, "Validation Exception occured :" + vex);

            throw vex;
        } catch (Exception ex) {
            knLogger.error( methodName, "Exception occured while Deleting Group: " +
                    ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Deleting Group", ex);
        }
        return result;
    }

    /**
     * @param ipGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnPubGroupInfoDTO getGroupDocDetails(KnIPPubGroupDTO ipGroupInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException, KnFWException {

        String methodName = "getGroupDocDetails(KnIPPubGroupDTO, persisterTxn)";
        boolean ownedTxn = false;
        boolean readOnly = true;
        KnPubGroupInfoPersistDTO groupPersistDTO = null;
        knLogger.debug( methodName, "ENTRY -> Input DTO Passed : " + ipGroupInfoDTO);

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            String ownerMdn = ipGroupInfoDTO.getOwner();
            String groupName= ipGroupInfoDTO.getGroupName();
            int groupDocEtag = ipGroupInfoDTO.getIfNoneMatch();

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipGroupInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
            String xcapUri = pubInfoUtil.constructGroupXcapUri(ownerMdn, groupName,subsProfile.getClientMajorVersion());

            String xdmServerId = subsProfile.getXdmsHome();
        //    String pocServerId = subsProfile.getPocHome();

            groupPersistDTO = new KnPubGroupInfoPersistDTO();
            groupPersistDTO.setInputDTO(ipGroupInfoDTO);

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setInputDTO(ipGroupInfoDTO);
            originator.setMdn(ownerMdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            originator.setMcpttID(subsProfile.getMcpttId());
            originator.setMcpttCompliance(subsProfile.getMcpttCompliance());
            groupPersistDTO.setPersistenceDTO(originator);

            knLogger.debug( methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(groupPersistDTO);
            knLogger.debug( methodName, "Authorized successfully.");

            // Populate Group persist dto with input dto
            pubInfoUtil.populateGroupInfoPersistDTO(ipGroupInfoDTO, groupPersistDTO);
            groupPersistDTO.setGroupDocURI(xcapUri);

            validatorFwk.validate(groupPersistDTO);
            knLogger.debug( methodName, "Validated successfully!");

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                                            KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            // get current etag for Group doc
            int etag = xdmServerDAO.getCurrentOMAGroupDocEtag(groupPersistDTO, true, persisterTxn);
            if (groupDocEtag > 0 && etag == groupDocEtag) {
                knLogger.error( methodName, "Etag mismatch while Get Group: " + etag);
                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_NOT_MODIFIED, "Group Not modified");
            }

            // get xmlStream
            InputStream is = xdmServerDAO.getCurrentOMAGroupDocXmlDoc(ownerMdn, xcapUri, 0,
                    readOnly, persisterTxn);
            String xmlDoc = null;
            xmlDoc = KnGeneralUtil.convertStreamToString(is);
            knLogger.debug( methodName, "XML Doc: " + xmlDoc + ", Etag: " + etag);
         //   groupPersistDTO.setStrXml(pubInfoUtil.decodeSpecialChars(xmlDoc));
               groupPersistDTO.setStrXml(xmlDoc);
            groupPersistDTO.setGroupDocEtag(etag);
            
            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.debug( methodName, "The mcpttid is"+KnGDPRTemplate.mcpttId(ipGroupInfoDTO.getMcPttId()));
        } catch (KnAASException aex) {
            knLogger.error( methodName, "Authorization Exception occured :" + aex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw aex;
        } catch (KnPubBOException ex) {
            knLogger.error( methodName, "PubBO Exception occured : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occured : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_DOES_NOT_EXISTS, "Group doesn't exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);
        } catch (KnValidationException vex) {
            knLogger.error( methodName, "Validation Exception occured :" + vex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error( methodName, "Exception occured while Retrieving Group: " +
                    ex.getMessage());
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Retrieving Group", ex);
        }
        return groupPersistDTO;
    }

    /**
     * @param ipGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnPubGroupInfoDTO getPubGroupDetails(KnIPPubGroupDTO ipGroupInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {
        String methodName = "getPubGroupDetails(KnIPPubGroupDTO, persisterTxn)";
        KnPubGroupInfoDTO result = null;
        knLogger.debug( methodName, "ENTRY -> Input DTO Passed : " + ipGroupInfoDTO);
        try {
            String ownerMdn = ipGroupInfoDTO.getOwner();
            String groupName= ipGroupInfoDTO.getGroupName();
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn, ipGroupInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);

            String xcapUri = pubInfoUtil.constructGroupXcapUri(ownerMdn, groupName,subsProfile.getClientMajorVersion());

            String xdmServerId = subsProfile.getXdmsHome();
            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(KnFactorySelector.DB).createXdmServerDAO(xdmServerId);            //
            int groupDocId = xdmServerDAO.getGroupDocId(xcapUri, persisterTxn);
            result = xdmServerDAO.getPubGroupDetails(groupDocId, ownerMdn, persisterTxn);
            knLogger.debug( methodName, "result :" + result );
            Collection<KnGroupMemberDTO> groupMemberList = result.getGroupMembers();
            knLogger.debug( methodName, "groupMemberList :" + groupMemberList );
            List<KnGroupMemberDTO> groupMemberListDetails = new ArrayList<>();
            List<String> mdnList=new ArrayList<>();
            for(KnGroupMemberDTO groupMember:groupMemberList){
                mdnList.add(groupMember.getMemberMdn());
            }
            Map<String, KnSubsProfilePersistDTO> subsMap = pubInfoUtil.getSubscriberDetails(mdnList, true, persisterTxn);
            knLogger.info(methodName, "subsMap : ", KnGDPRTemplate.mapKeyMdn(subsMap));

            for(KnGroupMemberDTO members:groupMemberList){
                if(subsMap.containsKey(members.getMemberMdn())){
                    members.setUfmi(subsMap.get(members.getMemberMdn()).getUfmi());
                    members.setUserId(subsMap.get(members.getMemberMdn()).getUserId());
                    members.setAliasMdn(subsMap.get(members.getMemberMdn()).getAliasMdn());
                }
                groupMemberListDetails.add(members);
            }

            result.setGroupMembers(groupMemberListDetails);
            result.setListServiceURI(pubInfoUtil.constructListServiceUri(ownerMdn,groupName));
        } catch (KnPubBOException ex) {
            knLogger.error( methodName, "PubBO Exception occured : " + ex);
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occured : " + ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_DOES_NOT_EXISTS, "Group doesn't exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);
        } catch (Exception ex) {
            knLogger.error( methodName, "Exception occured while Retrieving Group: " +
                    ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Retrieving Group", ex);
        }
        return result;
    }
    /**
     * @param ipGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnPubGroupInfoDTO getGroupDetails(KnIPPubGroupDTO ipGroupInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException, KnFWException {

        String methodName = "getGroupDetails(KnIPPubGroupDTO, persisterTxn)";
        boolean ownedTxn = false;
        boolean readOnly = true;
        KnPubGroupInfoPersistDTO groupPersistDTO = null;
        KnPubGroupInfoDTO result = null;
        knLogger.debug( methodName, "ENTRY -> Input DTO Passed : " + ipGroupInfoDTO);

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            String ownerMdn = ipGroupInfoDTO.getOwner();
//            String groupName= ipGroupInfoDTO.getGroupDisplayName();
//            int groupDocEtag = ipGroupInfoDTO.getIfNoneMatch();
//            String listServiceUri = ipGroupInfoDTO.getListServiceURI();
            int pocGroupId = ipGroupInfoDTO.getGroupId();
//            String xcapDocUri = pubInfoUtil.constructGroupXcapUri(ownerMdn, groupName);

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipGroupInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);

            String xdmServerId = subsProfile.getXdmsHome();
           // String pocServerId = subsProfile.getPocHome();

            groupPersistDTO = new KnPubGroupInfoPersistDTO();
            groupPersistDTO.setInputDTO(ipGroupInfoDTO);

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(ownerMdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            groupPersistDTO.setPersistenceDTO(originator);

            knLogger.debug( methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(groupPersistDTO);
            knLogger.debug( methodName, "Authorized successfully.");

            // Populate Group persist dto with input dto
            pubInfoUtil.populateGroupInfoPersistDTO(ipGroupInfoDTO, groupPersistDTO);
//            groupPersistDTO.setGroupDocURI(xcapDocUri);

//            validatorFwk.validate(groupPersistDTO);
//            knLogger.debug( methodName, "Validated successfully!");

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                                            KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            // get current etag for Group doc
//            int etag = xdmServerDAO.getCurrentOMAGroupDocEtag(groupPersistDTO, persisterTxn);
//            if (groupDocEtag > 0 && etag == groupDocEtag) {
//                knLogger.error( methodName, "Etag mismatch while Fetching Group doc: " + etag);
//                throw new KnPubBOException(KnErrorCodes.BOEntity.DOC_NOT_MODIFIED, "Group Doc Not modified");
//            }

            // get xmlStream
            result = xdmServerDAO.getGroupDetails(pocGroupId, ownerMdn, persisterTxn);
            knLogger.debug( methodName, "result :" + result );

            Collection<KnGroupMemberDTO> groupMemberList = result.getGroupMembers();
            for(KnGroupMemberDTO groupMember : groupMemberList){
                      //  groupMember.setMemberName(pubInfoUtil.decodeSpecialChars(groupMember.getMemberName()));
                          groupMember.setMemberName(groupMember.getMemberName());
              }
             knLogger.debug( methodName, "groupMemberList :" + groupMemberList );
            result.setGroupMembers(groupMemberList);

            if (ownedTxn) {
                persisterTxn.save();
            }
        } catch (KnAASException aex) {
            knLogger.error( methodName, "Authorization Exception occured :" + aex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw aex;
        } catch (KnPubBOException ex) {
            knLogger.error( methodName, "PubBO Exception occured : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occured : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.GROUP_DOES_NOT_EXISTS, "Group doesn't exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);
//        } catch (KnValidationException vex) {
//            knLogger.error( methodName, "Validation Exception occured :" + vex);
//            if (ownedTxn) rollback(persisterTxn);
//            throw vex;
        } catch (Exception ex) {
            knLogger.error( methodName, "Exception occured while Retrieving Group: " +
                    ex.getMessage());
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Retrieving Group", ex);
        }
        return result;
    }

    /**
     * @param ipGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public Collection<KnPubGroupDTO> getGroupList(KnIPPubGroupDTO ipGroupInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException, KnFWException {

        String methodName = "getGroupList(KnIPPubGroupDTO, persisterTxn)";
        KnOpPubResponse result = null;
        boolean ownedTxn = false;
        Collection<KnPubGroupDTO> groupList = null;
        KnPubGroupInfoPersistDTO groupPersistDTO = null;
        knLogger.debug( methodName, "ENTRY -> Input DTO Passed : " + ipGroupInfoDTO);

        try {
            //open a txn if its not already opened
            if (persisterTxn == null) {
                persisterTxn = KnPersisterTxn.getPersisterTxn();
                persisterTxn.open();
                ownedTxn = true;
            }

            String ownerMdn = ipGroupInfoDTO.getOwner();
          //  String groupName = ipGroupInfoDTO.getGroupName();

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipGroupInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);

            String xdmServerId = subsProfile.getXdmsHome();
          //  String pocServerId = subsProfile.getPocHome();

            groupPersistDTO = new KnPubGroupInfoPersistDTO();
            groupPersistDTO.setInputDTO(ipGroupInfoDTO);

            KnSubscriberPersistDTO originator = new KnSubscriberPersistDTO();
            originator.setMdn(ownerMdn);
            originator.setNetworkName(subsProfile.getNetworkName());
            originator.setPubSubscriptionType(subsProfile.getPublicSubscriptionType());
            originator.setServiceAuthStatus(subsProfile.getServiceAuthStatus());
            groupPersistDTO.setPersistenceDTO(originator);

            knLogger.debug( methodName, "Invoking Authorization.");
            // 5. Authorizing the subscriber
            authorizationFwk.authorize(groupPersistDTO);
            knLogger.debug( methodName, "Authorized successfully.");

            // Populate Group persist dto with input dto
//            groupPersistDTO = pubInfoUtil.populateGroupInfoPersistDTO(ipGroupInfoDTO);

//            validatorFwk.validate(groupPersistDTO);
//            knLogger.debug( methodName, "Validated successfully!");

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                                            KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            // get grouplist
            groupList = xdmServerDAO.getAllGroupDocsForMdn(ownerMdn, persisterTxn);

            if (groupList == null || groupList.isEmpty()) {
                knLogger.error( methodName, "No Group Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found.");
            }

            for(KnPubGroupDTO group :groupList )
            {
                   //group.setGroupDisplayName(pubInfoUtil.decodeSpecialChars(group.getGroupDisplayName()));
                   //group.setGroupName(pubInfoUtil.decodeSpecialChars(group.getGroupName()));
                group.setGroupDisplayName(group.getGroupDisplayName());
                group.setGroupName(group.getGroupName());

            }
            knLogger.debug( methodName, "groupDecode:"+groupList);


            int clientType = ipGroupInfoDTO.getClientType();
            if (com.kodiak.common.resources.KnConstants.CLIENT_TYPE_SOAP == clientType ) {
                Collection<Integer> groupDocIdList = new ArrayList<Integer>();
                for (KnPubGroupDTO grpDTO : groupList) {
                    groupDocIdList.add(grpDTO.getGroupDocId());
                }

                if (!groupDocIdList.isEmpty()) {
                    Map<Integer, Integer> grpDocVsGrpIdMap = xdmServerDAO.getPocGroupIdsForGroupDocIds(
                            groupDocIdList, true, persisterTxn);

                    if (grpDocVsGrpIdMap != null && !grpDocVsGrpIdMap.isEmpty()) {

                        for (KnPubGroupDTO grpDTO : groupList) {
                            int grpDocId = grpDTO.getGroupDocId();
                            int grpId = grpDocVsGrpIdMap.get(grpDocId);
                            grpDTO.setGroupId(grpId);
                        }
                    }
                }
            }

            if (ownedTxn) {
                persisterTxn.save();
            }
            knLogger.error( methodName, "Sending response back :" + groupList);
        } catch (KnAASException aex) {
            knLogger.error( methodName, "Authorization Exception occured :" + aex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw aex;
        } catch (KnPubBOException ex) {
            knLogger.error( methodName, "PubBO Exception occured : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error( methodName, "DAO Exception occured : " + ex);
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.NO_GROUPS_FOUND, "No Group(s) Found");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);
//        } catch (KnValidationException vex) {
//            knLogger.error( methodName, "Validation Exception occured :" + vex);
//            if (ownedTxn) rollback(persisterTxn);
//            throw vex;
        } catch (Exception ex) {
            knLogger.error( methodName, "Exception occured while Retrieving Groups: " +
                    ex.getMessage());
            if (ownedTxn) {
                rollback(persisterTxn);
            }
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Retrieving Groups", ex);
        }
        return groupList;
    }


    /**
     * @param ipGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public Collection<KnPubGroupDTO> getPubGroupList(KnIPPubGroupDTO ipGroupInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getPubGroupList(KnIPPubGroupDTO, persisterTxn)";
        Collection<KnPubGroupDTO> groupList = null;
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + ipGroupInfoDTO);
        try {
            String ownerMdn = ipGroupInfoDTO.getOwner();
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn, ipGroupInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);
            String xdmServerId = subsProfile.getXdmsHome();
            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            // get pubGroupList
            groupList = xdmServerDAO.getAllGroupDocsForMdn(ownerMdn, persisterTxn);
            if (groupList == null || groupList.isEmpty()) {
                knLogger.error(methodName, "No Group Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found.");
            }
            Collection<Integer> groupDocIdList = new ArrayList<Integer>();
            for (KnPubGroupDTO groupDTO : groupList) {
            	groupDTO.setGroupDisplayName(groupDTO.getGroupName());
                groupDTO.setGroupName(getGroupName(groupDTO.getGroupDocURI()));
                groupDocIdList.add(groupDTO.getGroupDocId());
            }
            knLogger.debug(methodName, "groupDecode:", groupList);
            if (!groupDocIdList.isEmpty()) {
                Map<Integer, Integer> grpDocVsGrpIdMap = xdmServerDAO.getPocGroupIdsForGroupDocIds(groupDocIdList, true, persisterTxn);
                if (grpDocVsGrpIdMap != null && !grpDocVsGrpIdMap.isEmpty()) {
                    for (KnPubGroupDTO grpDTO : groupList) {
                        int grpDocId = grpDTO.getGroupDocId();
                        int grpId = grpDocVsGrpIdMap.get(grpDocId);
                        grpDTO.setGroupId(grpId);
                    }
                }
            }

            knLogger.debug(methodName, "Sending response back :", groupList);
        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : " + ex);
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : " + ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.NO_GROUPS_FOUND, "No Group(s) Found");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while Retrieving Groups: " + ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Retrieving Groups", ex);
        }
        return groupList;
    }

    /**
     * Interface to retrieve the dynamic group details
     *
     * @param ipGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    @Override
    public KnPubGroupInfoDTO getDynamicGroupDetails(KnIPPubGroupInfoDTO ipGroupInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getDynamicGroupDetails(KnIPPubGroupDTO, persisterTxn)";
        KnPubGroupInfoPersistDTO groupPersistDTO;
        KnPubGroupInfoDTO result = new KnPubGroupInfoDTO();
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + ipGroupInfoDTO);

        try {
            String ownerMdn = ipGroupInfoDTO.getOwner();
            String displayName = ipGroupInfoDTO.getGroupDisplayName();
            String listServiceUri = pubInfoUtil.constructListServiceUri(ownerMdn, displayName);

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipGroupInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);

            KnDynamicGroupPersistDTO dynamicGrpPersistDTO = new KnDynamicGroupPersistDTO();
            dynamicGrpPersistDTO.setInputDTO(ipGroupInfoDTO);

            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String dynamicBasedFlgValue = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.DYNAPI_SERVICE_ENABLED.value());
             boolean dynamicFlag = false;
         	 if(null != dynamicBasedFlgValue) {
                 dynamicFlag = Integer.parseInt(dynamicBasedFlgValue) == ENABLED;
             }
            dynamicGrpPersistDTO.setDynAPIServFlag(dynamicFlag);
            dynamicGrpPersistDTO.setSubsProfile(subsProfile);

            knLogger.debug(methodName, "Invoking validation - ", dynamicGrpPersistDTO);
            validatorFwk.validate(dynamicGrpPersistDTO);
            knLogger.debug(methodName, "Validation completed ");

            String xdmServerId = subsProfile.getXdmsHome();

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            groupPersistDTO = xdmServerDAO.getOMAGroupDocForGroupName(ownerMdn, listServiceUri, displayName, persisterTxn);
            if (groupPersistDTO == null) {
                knLogger.error( methodName, "No Group Info found");
                throw new KnDAOException(KnErrorCodes.DAO.ROW_NOT_FOUND, "No Group Info found.");
            }

            KnPOCGroupDocMapDAO groupDocMapDAO = KnPubDBTablesRegistry.
                    getDBXdmTableRegistry().getPocGroupDocMapDAO(xdmServerId);
            int pocGroupId = groupDocMapDAO.getGroupIdForGroupDocId(groupPersistDTO.getGroupDocId(), ownerMdn, persisterTxn);

            KnPOCGroupMemberDAO pocGroupMemberDAO = KnPubDBTablesRegistry.
                    getDBXdmTableRegistry().getPocGroupMemberDAO(xdmServerId);
            //fetch the group members
            Collection<KnGroupMemberDTO> groupMemberList = pocGroupMemberDAO.getPocGroupMembers(pocGroupId, persisterTxn);

            if (null != groupMemberList && !groupMemberList.isEmpty()) {
                result.setGroupMembers(groupMemberList);
                result.setGroupMemberCount(groupMemberList.size());
            }

            result.setGroupDisplayName(groupPersistDTO.getGroupDisplayName());
            result.setGroupType(groupPersistDTO.getGroupType());
            result.setGroupDocEtag(groupPersistDTO.getGroupDocEtag());
            result.setGroupId(pocGroupId);
            result.setOwner(groupPersistDTO.getOwner());

            knLogger.debug(methodName, "result :" + result);

        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : " + ex);
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : " + ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.NO_GROUPS_FOUND, "Group doesn't exists");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);
        } catch (KnValidationException vex) {
            knLogger.error( methodName, "Validation Exception occured :" + vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        }catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while Retrieving Group: " +
                    ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Retrieving Group", ex);
        }
        return result;
    }

    /**
     * Interface to retrieve the list of groups associated with an owner MDN.
     *
     * @param ipGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    @Override
    public List<KnPubGroupDTO> getDynamicNonSharedGrpList(KnIPPubGroupInfoDTO ipGroupInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getDynamicNonSharedGrpList(KnIPPubGroupDTO, persisterTxn)";
        List<KnPubGroupDTO> groupList = null;
        //KnPubGroupInfoPersistDTO groupPersistDTO = null;
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + ipGroupInfoDTO);

        try {

            String ownerMdn = ipGroupInfoDTO.getOwner();

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    ipGroupInfoDTO.getProfile(), true, KnConstants.FALSE, persisterTxn);

            String xdmServerId = subsProfile.getXdmsHome();

            KnDynamicGroupPersistDTO dynamicGrpPersistDTO = new KnDynamicGroupPersistDTO();
            dynamicGrpPersistDTO.setInputDTO(ipGroupInfoDTO);

            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String dynamicBasedFlgValue = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.DYNAPI_SERVICE_ENABLED.value());
             boolean dynamicFlag = false;
         	 if(null != dynamicBasedFlgValue) {
                 dynamicFlag = Integer.parseInt(dynamicBasedFlgValue) == ENABLED;
             }
            dynamicGrpPersistDTO.setDynAPIServFlag(dynamicFlag);
            dynamicGrpPersistDTO.setSubsProfile(subsProfile);

            knLogger.debug(methodName, "Invoking validation - ", dynamicGrpPersistDTO);
            validatorFwk.validate(dynamicGrpPersistDTO);
            knLogger.debug(methodName, "Validation completed ");


            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXdmServerDAO(xdmServerId);
            // get grouplist
            Collection<KnPubGroupDTO> groups = xdmServerDAO.getAllGroupDocsForMdn(ownerMdn, persisterTxn);

            if (groups != null) {
                groupList = new ArrayList<>(groups);
            }

            knLogger.error(methodName, "Sending response back :" + groupList);
        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : " + ex);
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : " + ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.NO_GROUPS_FOUND, "No Group(s) Found");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);

        }catch (KnValidationException vex) {
            knLogger.error( methodName, "Validation Exception occured :" + vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        } catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while Retrieving Groups: " +
                    ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Retrieving Groups", ex);
        }
        return groupList;
    }

    /**
     * Interface to delete the non-shared group
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     */
    @Override
    public KnOpPubResponse deleteDynamicNonSharedGrp(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteDynamicNonSharedGrp(KnIPPubGroupDTO, persisterTxn)";
        KnOpPubResponse response = new KnOpPubResponse();
        //KnPubGroupInfoPersistDTO groupPersistDTO = null;
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + groupInfo);

        try {

            String ownerMdn = groupInfo.getOwner();
            String groupName= groupInfo.getGroupDisplayName();

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    groupInfo.getProfile(), true, KnConstants.FALSE, persisterTxn);

            String xcapDocUri = pubInfoUtil.constructGroupXcapUri(ownerMdn, groupName,subsProfile.getClientMajorVersion());

            String xdmServerId = subsProfile.getXdmsHome();

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            // Delete OMA Group doc
            KnDynamicGroupPersistDTO dynamicGrpPersistDTO = new KnDynamicGroupPersistDTO();
            dynamicGrpPersistDTO.setInputDTO(groupInfo);

            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String dynamicBasedFlgValue = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.DYNAPI_SERVICE_ENABLED.value());
             boolean dynamicFlag = false;
         	 if(null != dynamicBasedFlgValue) {
                 dynamicFlag = Integer.parseInt(dynamicBasedFlgValue) == ENABLED;
             }
            dynamicGrpPersistDTO.setDynAPIServFlag(dynamicFlag);
            dynamicGrpPersistDTO.setSubsProfile(subsProfile);

            knLogger.debug(methodName, "Invoking validation - ", dynamicGrpPersistDTO);
            validatorFwk.validate(dynamicGrpPersistDTO);
            knLogger.debug(methodName, "Validation completed ");
            // Get OMA Group doc Id from the xcap Doc Uri
            int groupDocId = xdmServerDAO.getGroupDocId(xcapDocUri, persisterTxn);

            xdmServerDAO.deleteOMAGroupDoc(groupDocId, persisterTxn);

            // get pocGroupId for groupDocId
            Collection<Integer> groupDocIds = new ArrayList<Integer>();
            groupDocIds.add(groupDocId);
            Map<Integer, Integer> groupDocVsGroupIdMap = xdmServerDAO.getPocGroupIdsForGroupDocIds(groupDocIds, true, persisterTxn);
            int pocGroupId = groupDocVsGroupIdMap.get(groupDocId);
            // Delete All PocGroupMembers
            xdmServerDAO.deleteAllPocGroupMembers(pocGroupId, persisterTxn);
            // Delete pocGroup
            xdmServerDAO.deletePocGroup(pocGroupId, persisterTxn);
            // delete PocGroupDocMap
            xdmServerDAO.deletePocGroupDocMap(pocGroupId, persisterTxn);

        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : " + ex);
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : " + ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.NO_GROUPS_FOUND, "No Group(s) Found");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);

        } catch (KnValidationException vex) {
            knLogger.error( methodName, "Validation Exception occured :" + vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        }catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while Retrieving Groups: " +
                    ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Retrieving Groups", ex);
        }
        return response;
    }

    /**
     * Interface to craete non-shared group
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     */
    @Override
    public KnOpPubResponse createNonSharedGroup(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "createNonSharedGroup(KnIPPubGroupDTO, persisterTxn)";
        KnOpPubResponse response = new KnOpPubResponse();
        //KnPubGroupInfoPersistDTO groupPersistDTO = null;
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + groupInfo);

        try {

            String ownerMdn = groupInfo.getOwner();
            String groupName = groupInfo.getGroupDisplayName();
            List<KnGroupMemberDTO> grpMembers = groupInfo.getGroupMembers();


            String listServiceURI = pubInfoUtil.constructListServiceUri(ownerMdn, groupName);

            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    groupInfo.getProfile(), true,KnConstants.FALSE, persisterTxn);

            String xcapDocUri = pubInfoUtil.constructGroupXcapUri(ownerMdn, groupName,subsProfile.getClientMajorVersion());

            String xdmServerId = subsProfile.getXdmsHome();

            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            //Get the third party ID from DG.THIRD_PARTY_ACCOUNT_INFO by THIRD_PARTY_ACCT_ID (request vendorId)
            int tpID = pubInfoUtil.getTPId(groupInfo.getVendorId(), persisterTxn);
            knLogger.info(methodName, "tpID - ", tpID);

            List<String> addedMdnList = new ArrayList<>();
            for (KnGroupMemberDTO mem : grpMembers) {
                addedMdnList.add(mem.getMemberMdn());
            }

            List<KnMemberDTO> addedMemberDetailList = new ArrayList<>();
            if (!addedMdnList.isEmpty()) {
                addedMemberDetailList = xdmServerDAO.getMembersDetails(addedMdnList, persisterTxn);
            }
            knLogger.debug(methodName, "addedMemberDetailList - ", addedMemberDetailList);

            //Get the list of MDN in the request vendorID from DG.THIRD_PARTY_USER_MDN_MAP table
            int tpClientType = KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value();
            int tpDispatchClientType = KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value();
            int mobileAPIClient = KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value();
            List<String> tpMdnList = new ArrayList<>();
            tpMdnList.add(ownerMdn);
            List<String> extContactList = new ArrayList<>();
            for(KnMemberDTO member : addedMemberDetailList){
                int memberClientType = Integer.parseInt(member.getClientType());
                if((tpClientType == memberClientType) || (tpDispatchClientType == memberClientType)
                        || (mobileAPIClient == memberClientType)){
                    tpMdnList.add(member.getMemberMdn());
                }
                if(subsProfile.getCorpId() != member.getCorpId()){
                    extContactList.add(member.getMemberMdn());
                }
            }
            Map<String, Integer> mdnTPidMap = xdmServerDAO.getMdnTPidMap(tpMdnList, persisterTxn);
            knLogger.debug(methodName, "mdnTPidMap - ", KnGDPRTemplate.mapKeyMdn(mdnTPidMap));

            //Get the valid external contact in the corporation
            List<String> validExtConts = xdmServerDAO.getExtContactList(extContactList, subsProfile.getCorpId(), persisterTxn);


            //Populating persistance DTO
            KnDynamicGroupPersistDTO dynamicGrpPersistDTO = new KnDynamicGroupPersistDTO();
            dynamicGrpPersistDTO.setInputDTO(groupInfo);
            dynamicGrpPersistDTO.setSubsProfile(subsProfile);
            dynamicGrpPersistDTO.setValidReqExtContactList(validExtConts);
            dynamicGrpPersistDTO.setTpID(tpID);
            dynamicGrpPersistDTO.setMdnTPidMap(mdnTPidMap);
            dynamicGrpPersistDTO.setReqExtContactList(extContactList);
            dynamicGrpPersistDTO.setMaxNumberOfMembers(pubInfoUtil.getXdmsServiceConfig().getMaxMembersPerPublicPOCGrp());
            dynamicGrpPersistDTO.setOwner(ownerMdn);
            dynamicGrpPersistDTO.setAddedMemberDetailList(addedMemberDetailList);
            dynamicGrpPersistDTO.setGroupDisplayName(groupInfo.getGroupDisplayName());
            dynamicGrpPersistDTO.setListServiceURI(listServiceURI);
            dynamicGrpPersistDTO.setGroupDocURI(xcapDocUri);
            dynamicGrpPersistDTO.setAddedMemList(addedMdnList);

            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String dynamicBasedFlgValue = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.DYNAPI_SERVICE_ENABLED.value());
             boolean dynamicFlag = false;
         	 if(null != dynamicBasedFlgValue) {
                 dynamicFlag = Integer.parseInt(dynamicBasedFlgValue) == ENABLED;
             }
            dynamicGrpPersistDTO.setDynAPIServFlag(dynamicFlag);
            dynamicGrpPersistDTO.setGroupType(groupInfo.getGroupType());
            dynamicGrpPersistDTO.setStrXml(groupInfo.getStrXml());
            knLogger.debug(methodName, "Invoking validation - ", dynamicGrpPersistDTO);
            validatorFwk.validate(dynamicGrpPersistDTO);
            knLogger.debug(methodName, "Validation completed ");

            knLogger.info(methodName, "Adding OMA Group Doc");
            xdmServerDAO.createOMAGroupDoc(dynamicGrpPersistDTO, persisterTxn);
            // create PocGroupDocMap
            knLogger.info(methodName, "Adding POC Group Doc Map");
            xdmServerDAO.createPocGroupDocMap(dynamicGrpPersistDTO, persisterTxn);
            // create pocGroup
            knLogger.info(methodName, "Adding POC Group");
            xdmServerDAO.createPOCGroup(dynamicGrpPersistDTO, persisterTxn);
            // add PocGroupMembers
            knLogger.info(methodName, "Adding POC Group Members");
            xdmServerDAO.addPocGroupMembers(dynamicGrpPersistDTO.getGroupId(), groupInfo.getGroupMembers(), persisterTxn);


        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : " + ex);
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : " + ex);
            if (ex instanceof KnDBConnectionException) {
                knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);

        } catch (KnValidationException vex) {
            knLogger.error( methodName, "Validation Exception occured :" + vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        }catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while Retrieving Groups: " +
                    ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Retrieving Groups", ex);
        }
        return response;
    }

    /**
     * Interface to modify non-shared group details - Add/modify/remove members are supported
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     */
    @Override
    public KnOpPubResponse modifyNonSharedGroup(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "modifyNonSharedGroup(KnIPPubGroupDTO, persisterTxn)";
        KnOpPubResponse response = new KnOpPubResponse();
        knLogger.debug(methodName, "ENTRY -> Input DTO Passed : " + groupInfo);
        try {
            String ownerMdn = groupInfo.getOwner();
            String groupName = groupInfo.getGroupDisplayName();
            String newGrpName = groupInfo.getNewGrpName();
            List<String> removedMembers = groupInfo.getRemovedMembers();
            String listServiceUri = pubInfoUtil.constructListServiceUri(ownerMdn, groupName);
            KnSubsProfileDTO subsProfile = pubInfoUtil.getProfileDetails(ownerMdn,
                    groupInfo.getProfile(), true, KnConstants.FALSE, persisterTxn);
            String xdmServerId = subsProfile.getXdmsHome();
            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(
                    KnFactorySelector.DB).createXdmServerDAO(xdmServerId);

            KnPubGroupInfoPersistDTO groupPersistDTO = xdmServerDAO.getOMAGroupDocForGroupName(ownerMdn, listServiceUri, groupName, persisterTxn);
            knLogger.debug(methodName, "groupPersistDTO " + groupPersistDTO);
            KnPOCGroupDocMapDAO groupDocMapDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getPocGroupDocMapDAO(xdmServerId);
            int pocGroupId = groupDocMapDAO.getGroupIdForGroupDocId(groupPersistDTO.getGroupDocId(), ownerMdn, persisterTxn);
            knLogger.info(methodName, "pocGroupId " + pocGroupId);
            KnPOCGroupMemberDAO pocGroupMemberDAO = KnPubDBTablesRegistry.getDBXdmTableRegistry().getPocGroupMemberDAO(xdmServerId);
            //fetch the group members
            Collection<KnGroupMemberDTO> existingMembers = pocGroupMemberDAO.getPocGroupMembers(pocGroupId, persisterTxn);
            knLogger.debug(methodName, "existingMembers " + existingMembers);


            //Get the third party ID from DG.THIRD_PARTY_ACCOUNT_INFO by THIRD_PARTY_ACCT_ID (request vendorId)
            int tpID = pubInfoUtil.getTPId(groupInfo.getVendorId(), persisterTxn);
            knLogger.info(methodName, "tpID - ", tpID);

            List<String> addedMdnList = new ArrayList<>();
            for (KnGroupMemberDTO mem : groupInfo.getGroupMembers()) {
                addedMdnList.add(mem.getMemberMdn());
            }

            List<KnMemberDTO> addedMemberDetailList = new ArrayList<>();
            if (!addedMdnList.isEmpty()) {
                addedMemberDetailList = xdmServerDAO.getMembersDetails(addedMdnList, persisterTxn);
            }
            knLogger.debug(methodName, "addedMemberDetailList - ", addedMemberDetailList);

            //Get the list of MDN in the request vendorID from DG.THIRD_PARTY_USER_MDN_MAP table
            int tpClientType = KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYPOCCLIENT.value();
            int tpDispatchClientType = KnConstants.SUBSCRIBERS_CLIENT_TYPE.THIRDPARTYDISPATCHERCLIENT.value();
            int mobileAPIClient = KnConstants.SUBSCRIBERS_CLIENT_TYPE.MOBILE_CLIENT.value();
            List<String> tpMdnList = new ArrayList<>();
            tpMdnList.add(ownerMdn);
            List<String> extContactList = new ArrayList<>();
            for (KnMemberDTO member : addedMemberDetailList) {
                int memberClientType = Integer.parseInt(member.getClientType());
                if ((tpClientType == memberClientType) || (tpDispatchClientType == memberClientType)
                        || (mobileAPIClient == memberClientType)) {
                    tpMdnList.add(member.getMemberMdn());
                }
                if (subsProfile.getCorpId() != member.getCorpId()) {
                    extContactList.add(member.getMemberMdn());
                }
            }
            Map<String, Integer> mdnTPidMap = xdmServerDAO.getMdnTPidMap(tpMdnList, persisterTxn);
            knLogger.debug(methodName, "mdnTPidMap - ", KnGDPRTemplate.mapKeyMdn(mdnTPidMap));

            //Get the valid external contact in the corporation
            List<String> validExtConts = xdmServerDAO.getExtContactList(extContactList, subsProfile.getCorpId(), persisterTxn);

            //Populating persistance DTO
            KnDynamicGroupPersistDTO dynamicGrpPersistDTO = populateDynamocGrpPersistDTO(groupInfo,subsProfile.getClientMajorVersion());
            dynamicGrpPersistDTO.setInputDTO(groupInfo);
            dynamicGrpPersistDTO.setSubsProfile(subsProfile);
            dynamicGrpPersistDTO.setValidReqExtContactList(validExtConts);
            dynamicGrpPersistDTO.setTpID(tpID);
            dynamicGrpPersistDTO.setMdnTPidMap(mdnTPidMap);
            dynamicGrpPersistDTO.setReqExtContactList(extContactList);
            dynamicGrpPersistDTO.setAddedMemberDetailList(addedMemberDetailList);
            dynamicGrpPersistDTO.setExistingMembers(existingMembers);
            dynamicGrpPersistDTO.setAddedMemList(addedMdnList);
            dynamicGrpPersistDTO.setGroupDocId(groupPersistDTO.getGroupDocId());
            dynamicGrpPersistDTO.setGroupId(pocGroupId);

            Map<String, String> microSvcCommonMap = genInfoUtil.retrieveMSSvcsCommonConfig(clusterId, persisterTxn);
            String dynamicBasedFlgValue = microSvcCommonMap.get(MICROSERVICES_COMMON_CONFIG.DYNAPI_SERVICE_ENABLED.value());
             boolean dynamicFlag = false;
         	 if(null != dynamicBasedFlgValue) {
                 dynamicFlag = Integer.parseInt(dynamicBasedFlgValue) == ENABLED;
             }
         	dynamicGrpPersistDTO.setDynAPIServFlag(dynamicFlag);

            knLogger.debug(methodName, "Invoking validation - ", dynamicGrpPersistDTO);
            validatorFwk.validate(dynamicGrpPersistDTO);
            knLogger.debug(methodName, "Validation completed ");

            if ((newGrpName != null) && (!groupName.equals(newGrpName))) {
                updateDynamicGrpName(xdmServerDAO, dynamicGrpPersistDTO, groupPersistDTO, persisterTxn, newGrpName);
            }
            knLogger.info(methodName, "Adding POC Group Members");
            xdmServerDAO.addPocGroupMembers(dynamicGrpPersistDTO.getGroupId(), groupInfo.getGroupMembers(), persisterTxn);
            knLogger.info(methodName, "Deleting POC Group Members");
            xdmServerDAO.deletePocGroupMembers(dynamicGrpPersistDTO.getGroupId(), removedMembers, persisterTxn);
            knLogger.info(methodName, "Updating POC Group Members");
            xdmServerDAO.modifyPocGroupMembers(dynamicGrpPersistDTO.getGroupId(), groupInfo.getModifiedMembers(), persisterTxn);


        } catch (KnPubBOException ex) {
            knLogger.error(methodName, "PubBO Exception occured : " + ex);
            throw ex;
        } catch (KnDAOException ex) {
            knLogger.error(methodName, "DAO Exception occured : " + ex);
            if (KnErrorCodes.DAO.ROW_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new KnXDMServerException(KnErrorCodes.BOEntity.NO_GROUPS_FOUND, "No Group(s) Found");
            } else if (ex instanceof KnDBConnectionException) {
                knLogger.error(methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR,
                    "Persistence exception occured : ", ex);

        } catch (KnValidationException vex) {
            knLogger.error( methodName, "Validation Exception occured :" + vex);
            throw new KnXDMServerException(vex.getErrorCode(), vex.getErrorMessage());
        }catch (Exception ex) {
            knLogger.error(methodName, "Exception occured while Retrieving Groups: " +
                    ex.getMessage());
            throw new KnSystemException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Unexpected exception occured while " +
                    "Retrieving Groups", ex);
        }
        return response;
    }

    /**
     * Private method to populate the DynamicGroupPersistDTO
     * @param groupInfo
     * @return
     */
    private KnDynamicGroupPersistDTO populateDynamocGrpPersistDTO(KnIPPubGroupInfoDTO groupInfo, int pv) {
        KnDynamicGroupPersistDTO dynamicGrpPersistDTO = new KnDynamicGroupPersistDTO();
        dynamicGrpPersistDTO.setMaxNumberOfMembers(pubInfoUtil.getXdmsServiceConfig().getMaxMembersPerPublicPOCGrp());
        dynamicGrpPersistDTO.setOwner(groupInfo.getOwner());
        List<KnMemberDTO> modifiedMdnList = new ArrayList<>();
        for (KnGroupMemberDTO mem : groupInfo.getModifiedMembers()) {
            KnMemberDTO memberDTO = new KnMemberDTO();
            memberDTO.setMemberMdn(mem.getMemberMdn());
            memberDTO.setMemberName(mem.getMemberName());
            modifiedMdnList.add(memberDTO);
        }
        dynamicGrpPersistDTO.setRemovedMembers(groupInfo.getRemovedMembers());
        dynamicGrpPersistDTO.setModifiedMemberDetailList(modifiedMdnList);
        if ((groupInfo.getNewGrpName()!=null) && (!groupInfo.getGroupDisplayName().equals(groupInfo.getNewGrpName()))) {
            String newListServiceUri = pubInfoUtil.constructListServiceUri(groupInfo.getOwner(), groupInfo.getNewGrpName());
            dynamicGrpPersistDTO.setGroupName(groupInfo.getNewGrpName());
            dynamicGrpPersistDTO.setGroupDisplayName(groupInfo.getNewGrpName());
            dynamicGrpPersistDTO.setListServiceURI(newListServiceUri);
            String xcapDocUri = pubInfoUtil.constructGroupXcapUri(groupInfo.getOwner(), groupInfo.getNewGrpName(),pv);
            dynamicGrpPersistDTO.setGroupDocURI(xcapDocUri);
        }else{
            String listServiceUri = pubInfoUtil.constructListServiceUri(groupInfo.getOwner(), null);
            dynamicGrpPersistDTO.setListServiceURI(listServiceUri);
        }
        return dynamicGrpPersistDTO;
    }


    /**
     * Rollback the transaction
     *
     * @param txn transaction object
     */
    private void rollback(KnPersisterTxn txn) {
        try {
            txn.rollback();
        } catch (Exception e) {
            knLogger.debug( "rollback(txn)", "Failed to rollback the transaction.");
        }
    }

    private String getGroupName(String documentSelector) {
        String groupName = null;
        int documentNameSeparator = documentSelector.lastIndexOf("/");
        if (documentNameSeparator != -1) {
            groupName = documentSelector.substring(documentNameSeparator + 1).split("\\.")[0];
        }
        return groupName;
    }

    /**
     * Private method to update the group display name and related columns
     * @param xdmServerDAO
     * @param dynamicGrpPersistDTO
     * @param groupPersistDTO
     * @param persisterTxn
     * @param newGrpName
     * @throws KnDAOException
     */

    private void updateDynamicGrpName(IPubXdmDAO xdmServerDAO, KnDynamicGroupPersistDTO dynamicGrpPersistDTO, KnPubGroupInfoPersistDTO
            groupPersistDTO, KnPersisterTxn persisterTxn, String newGrpName) throws KnDAOException {
        String methodName = "modifyNonSharedGroup(KnIPPubGroupDTO, persisterTxn)";
        groupPersistDTO.setGroupDisplayName(newGrpName);
        groupPersistDTO.setListServiceURI(dynamicGrpPersistDTO.getListServiceURI());
        groupPersistDTO.setGroupDocURI(dynamicGrpPersistDTO.getGroupDocURI());
        knLogger.info(methodName, "Modify OMA Group Doc");
        xdmServerDAO.updateOMAGroupDetail(groupPersistDTO, persisterTxn);
        knLogger.info(methodName, "Update POC Group");
        List<KnPubGroupPersistDTO> grpList = new ArrayList<>();
        KnPubGroupPersistDTO group = new KnPubGroupPersistDTO();
        group.setListServiceURI(dynamicGrpPersistDTO.getListServiceURI());
        group.setOwner(dynamicGrpPersistDTO.getOwner());
        group.setGroupDocId(dynamicGrpPersistDTO.getGroupDocId());
        grpList.add(group);
        knLogger.info(methodName, "Update ListService URI");
        xdmServerDAO.updatePocGroupsListSrvUri(grpList, persisterTxn);
        knLogger.exit();
    }

    public void modifyGroupUriContext(KnIPPubGroupInfoDTO inputDTO, String xdmpttserverId, KnPersisterTxn persisterTxn) throws KnXDMServerException{
        final String methodName = "modifyGroupUriContext(KnIPPubAuthListDTO, persisterTxn)";
        try {
            knLogger.debug( methodName, "ENTRY -> Input DTO Passed : " + inputDTO);
            IPubXdmDAO xdmServerDAO = KnPubFactorySelector.getDAOFactory(KnFactorySelector.DB).createXdmServerDAO(xdmpttserverId);
            // get pubGroupList
            Collection<KnPubGroupDTO> groupList = xdmServerDAO.getAllGroupDocsForMdn(inputDTO.getOwner(), persisterTxn);
            knLogger.debug( methodName, "groupList fetched : " + groupList);
            List<KnPubGroupDTO> newGroupList = new ArrayList<>();
            if(groupList != null) {

                for (KnPubGroupDTO pubGroupDTO : groupList) {
                    String uri = pubGroupDTO.getGroupDocURI();
                    String newDocUri = uri.replaceFirst(XCAP_ROOT, OIDC_XCAP_ROOT);
                    pubGroupDTO.setGroupDocURI(newDocUri);
                    newGroupList.add(pubGroupDTO);
                }

                knLogger.debug(methodName, "after replace group list is  : " + newGroupList);

                // update all oma group docs
                xdmServerDAO.updateAllOMAGroupDocs(newGroupList, persisterTxn);
            }
            knLogger.debug( methodName, "Update is successfull  : " + newGroupList);

        }catch(Exception ex){
            if (ex instanceof KnDBConnectionException) {
                knLogger.error( methodName, "DAO DBConnection Exception occured :" + ex);
                throw new KnXDMServerException(KnErrorCodes.BOEntity.PTT_SERVER_NOT_REACHABLE, ex.getMessage(), ex);
            }
            throw new KnXDMServerException(KnErrorCodes.BOEntity.INTERNAL_ERROR, "Persistence exception occured : ", ex);
        }
    }
}
