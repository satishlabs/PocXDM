/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.helper;

import com.kodiak.common.commdto.common.*;
import com.kodiak.common.commdto.request.*;
import com.kodiak.common.commdto.response.*;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.exception.KnException;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.common.resources.KnGDPRTemplate;
import com.kodiak.frameworks.messaging.common.dto.KnMqServiceConfig;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.mediator.KnMediatorConstants;
import com.kodiak.xdms.notificationmgr.IXcapDiffNotifierIntf;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffDocDTO;
import com.kodiak.xdms.notificationmgr.beans.KnXcapDiffNotifyDTO;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifier;
import com.kodiak.xdms.notificationmgr.impl.KnXcapDiffNotifierImpl;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.dto.clientdat.KnIPChangeMDNInfoDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDirChgDTO;
import com.kodiak.xdms.server.common.dto.clientdat.KnOPDocChgDTO;
import com.kodiak.xdms.server.common.dto.common.KnCorpGpInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMCSXCAPRespDTO;
import com.kodiak.xdms.server.corpmgmt.clientintf.ICorpClientIntf;
import com.kodiak.xdms.server.corpmgmt.clientintf.impl.KnCorpClientImpl;
import com.kodiak.xdms.server.corpmgmt.dto.clientdat.KnIPTalkGroupDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnCorpResponseDTO;
import com.kodiak.xdms.server.corpmgmt.dto.impl.KnXDMTalkGroupServerRespDTO;
import com.kodiak.xdms.server.pubmgmt.clientintf.IPubClientIntf;
import com.kodiak.xdms.server.pubmgmt.clientintf.impl.KnPubClientIntf;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnEmergencyMdnDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.*;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubContactDTO;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubGroupDTO;

import java.lang.Integer;
import java.util.*;
import java.util.ArrayList;

import static com.kodiak.common.resources.KnConstants.*;
import static com.kodiak.common.resources.KnConstants.FEATURE_SET.HTTPSSUPPORT;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMPubDocMediator.java
 * Subsystem:  Mediator
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 14, 2011           7.0
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

/**
 * This class supports operations from XCAP wrapper client
 */
public class KnXDMPubDocMediator {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMPubDocMediator.class);

    IPubClientIntf pubClientIntf = null;
    private static KnXDMPubDocMediator instance = new KnXDMPubDocMediator();
    private String xcapRootUri = null;
    private ICorpClientIntf corpClientIntf;
    private KnXDMCommonMediator commonMediator;

    IXcapDiffNotifierIntf notifier;

    private KnXDMPubDocMediator() {
        pubClientIntf = new KnPubClientIntf();
        corpClientIntf = new KnCorpClientImpl();
        commonMediator = KnXDMCommonMediator.getInstance();
        notifier = new KnXcapDiffNotifierImpl();
    }

    public static KnXDMPubDocMediator getInstance() {
        return instance;
    }

    /**
     * @param pubContactInfoDTO KnXDMContactListInfoDTO
     * @param persisterTxn      KnPersisterTxn
     * @return KnOpPubResponse
     * @throws KnXDMServerException
     */
    public KnOpPubResponse addContacts(KnXDMContactListInfoDTO pubContactInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "addContacts";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubContactInfoDTO inputDTO = new KnIPPubContactInfoDTO();
        inputDTO.setOwner(pubContactInfoDTO.getOwnerMdn());
        inputDTO.setContactListDisplayName(pubContactInfoDTO.getContactListDisplayName());
        inputDTO.setContactEntryUri(pubContactInfoDTO.getContactEntryUri());
        inputDTO.setContactListName(pubContactInfoDTO.getContactListDisplayName());
        inputDTO.setContactListType(pubContactInfoDTO.getContactListType());
        inputDTO.setIfMatch(pubContactInfoDTO.getIfMatch());
        inputDTO.setIfNoneMatch(pubContactInfoDTO.getIfNoneMatch());

        List<KnMemberDTO> listMems = new ArrayList<KnMemberDTO>();
        for (KnXDMMdnInfoDTO member : pubContactInfoDTO.getMembers()) {
            KnMemberDTO listMem = new KnMemberDTO();
            listMem.setMemberMdn(member.getMdn());
            listMem.setMemberName(member.getName());
            listMem.setUfmi(member.getUfmi());
            listMems.add(listMem);
        }
        inputDTO.setMembers(listMems);

        Collection<KnMemberDetailsDTO> memDetails = new ArrayList<KnMemberDetailsDTO>();
        for (KnXDMMemberDTO mem : pubContactInfoDTO.getMemberDetails()) {
            KnMemberDetailsDTO memDetail = new KnMemberDetailsDTO();
            memDetail.setUri(mem.getUri());
            memDetail.setDisplayName(mem.getDisplayName());
            memDetail.setUfmi(mem.getUfmi());
            memDetails.add(memDetail);
        }
        inputDTO.setMemberDetails(memDetails);
        inputDTO.setContactAddonAliasMap(pubContactInfoDTO.getContactAddonAliasMap());

        inputDTO.setClientType(pubContactInfoDTO.getClientType());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnOpPubResponse response;
        response = pubClientIntf.addContacts(inputDTO, persisterTxn);
        return response;
    }


    /**
     * @param pubContactInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMPubRespDTO modifyContacts(KnXDMContactListInfoDTO pubContactInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "modifyContacts";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubContactInfoDTO inputDTO = new KnIPPubContactInfoDTO();
        inputDTO.setOwner(pubContactInfoDTO.getOwnerMdn());
        inputDTO.setContactListDisplayName(pubContactInfoDTO.getContactListDisplayName());
        inputDTO.setContactListName(pubContactInfoDTO.getContactListDisplayName());
        inputDTO.setContactListType(pubContactInfoDTO.getContactListType());
        inputDTO.setContactEntryUri(pubContactInfoDTO.getContactEntryUri());
        inputDTO.setIfMatch(pubContactInfoDTO.getIfMatch());
        inputDTO.setIfNoneMatch(pubContactInfoDTO.getIfNoneMatch());
        inputDTO.setMcpttId(pubContactInfoDTO.getMcpttId());
        List<KnMemberDTO> listMems = new ArrayList<KnMemberDTO>();
        for (KnXDMMdnInfoDTO member : pubContactInfoDTO.getMembers()) {
            KnMemberDTO listMem = new KnMemberDTO();
            listMem.setMemberMdn(member.getMdn());
            listMem.setMemberName(member.getName());
            listMem.setUfmi(member.getUfmi());

            listMems.add(listMem);
        }
        inputDTO.setMembers(listMems);

        Collection<KnMemberDetailsDTO> memDetails = new ArrayList<KnMemberDetailsDTO>();
        for (KnXDMMemberDTO mem : pubContactInfoDTO.getMemberDetails()) {
            KnMemberDetailsDTO memDetail = new KnMemberDetailsDTO();
            memDetail.setUri(mem.getUri());
            memDetail.setDisplayName(mem.getDisplayName());
            //memDetail.setUfmi(mem.getUfmi());
            memDetails.add(memDetail);
        }
        inputDTO.setMemberDetails(memDetails);

        inputDTO.setClientType(pubContactInfoDTO.getClientType());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnOpPubResponse response = pubClientIntf.modifyContacts(inputDTO, persisterTxn);

        knLogger.debug( methodName, "ResponseDTO:  ", response);

        sendNotification(response.getDirChgDTO(), persisterTxn);
        KnXDMPubRespDTO pubResp = new KnXDMPubRespDTO();

        pubResp.setResponseCode(response.getResponseCode());
        pubResp.setResponseMessage(response.getResponseMessage());
        pubResp.setResponseStatus(response.getResponseStatus());

        pubResp.setDocEtag(response.getDocEtag());

        knLogger.debug( methodName, "EXIT: ResponseDTO:  ", pubResp);
        return pubResp;
    }


    /**
     * @param pubContactInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse deleteContacts(KnXDMContactListInfoDTO pubContactInfoDTO,
                                          KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "deleteContacts";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubContactInfoDTO inputDTO = new KnIPPubContactInfoDTO();
        inputDTO.setOwner(pubContactInfoDTO.getOwnerMdn());
        inputDTO.setContactListDisplayName(pubContactInfoDTO.getContactListDisplayName());
        inputDTO.setContactEntryUri(pubContactInfoDTO.getContactEntryUri());
        inputDTO.setContactListName(pubContactInfoDTO.getContactListDisplayName());
        inputDTO.setContactListType(pubContactInfoDTO.getContactListType());
        inputDTO.setIfMatch(pubContactInfoDTO.getIfMatch());
        inputDTO.setIfNoneMatch(pubContactInfoDTO.getIfNoneMatch());
        inputDTO.setMcpttId(pubContactInfoDTO.getMcpttId());


        List<KnMemberDTO> listMems = new ArrayList<KnMemberDTO>();
        for (KnXDMMdnInfoDTO member : pubContactInfoDTO.getMembers()) {
            KnMemberDTO listMem = new KnMemberDTO();
            listMem.setMemberMdn(member.getMdn());
            listMem.setMemberName(member.getName());

            listMems.add(listMem);
        }
        inputDTO.setMembers(listMems);

        Collection<KnMemberDetailsDTO> memDetails = new ArrayList<KnMemberDetailsDTO>();
        for (KnXDMMemberDTO mem : pubContactInfoDTO.getMemberDetails()) {
            KnMemberDetailsDTO memDetail = new KnMemberDetailsDTO();
            memDetail.setUri(mem.getUri());
            memDetail.setDisplayName(mem.getDisplayName());

            memDetails.add(memDetail);
        }
        inputDTO.setMemberDetails(memDetails);

        inputDTO.setClientType(pubContactInfoDTO.getClientType());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnOpPubResponse response = pubClientIntf.deleteContacts(inputDTO, persisterTxn);


        knLogger.debug(methodName, "EXIT: ResponseDTO:  ", response);
        return response;
    }


    /**
     * @param pubContactInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMContactListInfoRespDTO getContactListDetails(KnXDMContactListInfoDTO
                                                                     pubContactInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "getContactListDetails";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubContactInfoDTO inputDTO = new KnIPPubContactInfoDTO();
        KnPubContactDTO response=new KnPubContactDTO();
        
        inputDTO.setOwner(pubContactInfoDTO.getOwnerMdn());
        inputDTO.setContactListDisplayName(pubContactInfoDTO.getContactListDisplayName());
        inputDTO.setContactListName(pubContactInfoDTO.getContactListDisplayName());
        inputDTO.setContactListType(pubContactInfoDTO.getContactListType());
        inputDTO.setClientType(pubContactInfoDTO.getClientType());
        inputDTO.setIfMatch(pubContactInfoDTO.getIfMatch());
        inputDTO.setIfNoneMatch(pubContactInfoDTO.getIfNoneMatch());
        inputDTO.setMcpttId(pubContactInfoDTO.getMcpttId());
        
        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        if(pubContactInfoDTO.getClientType()!=KnConstants.PTX_XDMDATA_INTF) {
        	response = pubClientIntf.getContactListDetails(inputDTO, persisterTxn);
        }else {
        	knLogger.debug(methodName,"getXdmintfContactListDetails : ClientType",pubContactInfoDTO.getClientType());
        	response = pubClientIntf.getXdmintfContactListDetails(inputDTO, persisterTxn);
        }
        

        knLogger.debug( methodName, "ResponseDTO:  ", response);
        KnXDMContactListInfoRespDTO pubResp = new KnXDMContactListInfoRespDTO();

        pubResp.setContactListName(response.getContactListName());
        pubResp.setContactListDisplayName(response.getContactListDisplayName());

        pubResp.setContactListType(response.getContactListType());
        pubResp.setContactsList(response.getContactsList());
        pubResp.setStrXml(response.getStrXml());
        pubResp.setEtag(response.getEtag());

        knLogger.debug( methodName, "EXIT: ResponseDTO:  ", pubResp);
        return pubResp;
    }


    /**
     * @param pubSubsInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnAppInfoDTO getAllContactLists(KnXDMSubsInfoDTO pubSubsInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "getAllContactLists";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubContactDTO inputDTO = new KnIPPubContactDTO();
        inputDTO.setOwner(pubSubsInfoDTO.getMdn());
        inputDTO.setClientType(pubSubsInfoDTO.getClientType());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        Collection<KnPubContactInfoDTO> responseDTOs = pubClientIntf.getAllContactLists(inputDTO, persisterTxn);

        knLogger.debug( methodName, "ResponseDTOS:  ", responseDTOs);

        KnAppInfoDTO medRespDTO = new KnAppInfoDTO();
        Collection<KnAppDetailsDTO> medRespDetailsDTOs = new ArrayList<KnAppDetailsDTO>();


        for (KnPubContactDTO responseDTO : responseDTOs) {
            KnAppDetailsDTO medRespDetailsDTO = new KnAppDetailsDTO();
            medRespDetailsDTO.setAuid(KnConstants.APP_UID_PUBLIC_CONTACT);
            medRespDetailsDTO.setDocName(KnConstants.XCAP_INDEX);

            medRespDetailsDTO.setEtag(responseDTO.getEtag());
            medRespDetailsDTO.setXcapRoot(KnConstants.XCAP_ROOT);

            medRespDetailsDTOs.add(medRespDetailsDTO);
        }

        medRespDTO.setAuid(KnConstants.APP_UID_PUBLIC_CONTACT);
        medRespDTO.setAppDetailsList(medRespDetailsDTOs);

        knLogger.debug( methodName, "EXIT: ResponseDTO:  ", medRespDTO);
        return medRespDTO;
    }


    /**
     * @param pubSubsInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMDirectoryRespDTO getIndexDetails(KnXDMDirectoryInfoDTO pubSubsInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "getIndexDetails";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubContactDTO inputDTO = new KnIPPubContactDTO();
        inputDTO.setOwner(pubSubsInfoDTO.getMdn());
        inputDTO.setIfMatch(pubSubsInfoDTO.getIfMatch());
        inputDTO.setIfNoneMatch(pubSubsInfoDTO.getIfNoneMatch());
        inputDTO.setClientType(pubSubsInfoDTO.getClientType());
        inputDTO.setMcpttId(pubSubsInfoDTO.getMcPttid());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnOPPubDirResponse responseDTO = pubClientIntf.getIndexDetails(inputDTO, persisterTxn);

        knLogger.debug( methodName, "ResponseDTO:  ", responseDTO);
        xcapRootUri = responseDTO.getXcapRootUri();
        if (xcapRootUri == null || xcapRootUri.equals("")) {
            xcapRootUri = KnConstants.XCAP_ROOT;
        }

        KnXDMDirectoryRespDTO medDirRespDTO = new KnXDMDirectoryRespDTO();
        Collection<KnAppInfoDTO> appInfoList = new ArrayList<KnAppInfoDTO>();

        // Subs Config doc details
        KnAppInfoDTO medSubsRespDTO = new KnAppInfoDTO();
        Collection<KnAppDetailsDTO> medSubsRespDetailsDTOs = new ArrayList<KnAppDetailsDTO>();

        KnAppDetailsDTO medSubsRespDetailsDTO = new KnAppDetailsDTO();
        medSubsRespDetailsDTO.setAuid(KnConstants.APP_UID_SUBSCRIBER_CONFIG);
        medSubsRespDetailsDTO.setDocName(KnConstants.XCAP_INDEX);

        medSubsRespDetailsDTO.setEtag(responseDTO.getSubsUpdateTime());
        medSubsRespDetailsDTO.setXcapRoot(xcapRootUri);
        medSubsRespDetailsDTOs.add(medSubsRespDetailsDTO);

        medSubsRespDTO.setAuid(KnConstants.APP_UID_SUBSCRIBER_CONFIG);
        medSubsRespDTO.setAppDetailsList(medSubsRespDetailsDTOs);
        appInfoList.add(medSubsRespDTO);


       /*  TGSC  doc details
        KnAppInfoDTO medTGSCRespDTO = new KnAppInfoDTO();
        Collection<KnAppDetailsDTO> medTGSCDetailsDTOs = new ArrayList<KnAppDetailsDTO>();

        KnAppDetailsDTO medTgscRespDetailsDTO = new KnAppDetailsDTO();
        medTgscRespDetailsDTO.setAuid(KnConstants.APP_UID_PUB_TGSC);
        medTgscRespDetailsDTO.setDocName(KnConstants.XCAP_INDEX);

        medTgscRespDetailsDTO.setEtag(responseDTO.getSubsUpdateTime());
        medTgscRespDetailsDTO.setXcapRoot(xcapRootUri);
        medTGSCDetailsDTOs.add(medTgscRespDetailsDTO);

        medTGSCRespDTO.setAuid(KnConstants.APP_UID_PUB_TGSC);
        medTGSCRespDTO.setAppDetailsList(medTGSCDetailsDTOs);
        appInfoList.add(medTGSCRespDTO);
        */

        // RLS doc details
        KnAppInfoDTO medRlsRespDTO = new KnAppInfoDTO();
        Collection<KnAppDetailsDTO> medRlsRespDetailsDTOs = new ArrayList<KnAppDetailsDTO>();

        KnAppDetailsDTO medRlsRespDetailsDTO = new KnAppDetailsDTO();
        medRlsRespDetailsDTO.setAuid(KnConstants.APP_UID_RLS_SERVICES);
        medRlsRespDetailsDTO.setDocName(KnConstants.XCAP_INDEX);

        medRlsRespDetailsDTO.setEtag(responseDTO.getRlsEtag());
        medRlsRespDetailsDTO.setXcapRoot(xcapRootUri);


        medRlsRespDetailsDTOs.add(medRlsRespDetailsDTO);

        medRlsRespDTO.setAuid(KnConstants.APP_UID_RLS_SERVICES);
        medRlsRespDTO.setAppDetailsList(medRlsRespDetailsDTOs);
        appInfoList.add(medRlsRespDTO);

        // Pub Resource List
        KnAppInfoDTO medRespDTO = new KnAppInfoDTO();
        Collection<KnAppDetailsDTO> medRespDetailsDTOs = new ArrayList<KnAppDetailsDTO>();
        KnAppDetailsDTO medRespDetailsDTO = new KnAppDetailsDTO();

        if (responseDTO.getPublicSubsType() == 1) {
            medRespDetailsDTO.setAuid(KnConstants.APP_UID_PUBLIC_CONTACT);
            medRespDetailsDTO.setDocName(KnConstants.XCAP_INDEX);

            medRespDetailsDTO.setEtag(responseDTO.getContactListDTO().getEtag());

            medRespDetailsDTO.setXcapRoot(xcapRootUri);

            medRespDetailsDTOs.add(medRespDetailsDTO);
        }
        medRespDTO.setAuid(KnConstants.APP_UID_PUBLIC_CONTACT);
        medRespDTO.setAppDetailsList(medRespDetailsDTOs);

        appInfoList.add(medRespDTO);

        // Group related
        Collection<KnPubGroupDTO> grpResponseDTOs = responseDTO.getGroupList();
        if (grpResponseDTOs != null && !grpResponseDTOs.isEmpty()) {
            KnAppInfoDTO medGrpRespDTO = new KnAppInfoDTO();
            Collection<KnAppDetailsDTO> medGrpRespDetailsDTOs = new ArrayList<KnAppDetailsDTO>();

            for (KnPubGroupDTO grpResponseDTO : grpResponseDTOs) {
                KnAppDetailsDTO medGrpRespDetailsDTO = new KnAppDetailsDTO();
                medGrpRespDetailsDTO.setAuid(KnConstants.APP_UID_PUBLIC_GROUP);
                String docUri = grpResponseDTO.getGroupDocURI();
                knLogger.debug( methodName, "Grp Doc URI:", docUri);
                String grpName = null;
                if (docUri == null || docUri.equals("")) {
                    knLogger.debug( methodName, "Setting Group Display Name as Grp Name:", grpResponseDTO.getGroupName());
                    medGrpRespDetailsDTO.setDocName(grpResponseDTO.getGroupName());
                } else {
                    grpName = docUri.substring(docUri.lastIndexOf("/") + 1);
                    knLogger.debug( methodName, "Grp Name fetched from Doc URI:", grpName);
                    medGrpRespDetailsDTO.setDocName(grpName);
                }

                medGrpRespDetailsDTO.setEtag(grpResponseDTO.getGroupDocEtag());

                medGrpRespDetailsDTO.setXcapRoot(xcapRootUri);
                medGrpRespDetailsDTOs.add(medGrpRespDetailsDTO);
            }

            medGrpRespDTO.setAuid(KnConstants.APP_UID_PUBLIC_GROUP);
            medGrpRespDTO.setAppDetailsList(medGrpRespDetailsDTOs);

            appInfoList.add(medGrpRespDTO);
        }
        // Group related

        //Setting external corp groups for public subscriber
        Collection<KnCorpGpInfoDTO> corpGrpResponseDTOs = responseDTO.getCorporateGroupList();
        if (corpGrpResponseDTOs != null && !corpGrpResponseDTOs.isEmpty()) {
            KnAppInfoDTO corpGrpRespDTO = new KnAppInfoDTO();
            Collection<KnAppDetailsDTO> medGrpRespDetailsDTOs = new ArrayList<KnAppDetailsDTO>();
            for (KnCorpGpInfoDTO corpGrpRespDto : corpGrpResponseDTOs) {
                KnAppDetailsDTO medGrpRespDetailsDTO = new KnAppDetailsDTO();
                medGrpRespDetailsDTO.setAuid(KnConstants.APP_UID_CORP_GROUP);
                medGrpRespDetailsDTO.setDocName(String.valueOf(corpGrpRespDto.getCorpgroupId()));
                medGrpRespDetailsDTO.setEtag(corpGrpRespDto.getGroupEtag());
                medGrpRespDetailsDTO.setGroupCreatedBy(corpGrpRespDto.getGroupCreatedBy());
                medGrpRespDetailsDTO.setMcxGroupInd(corpGrpRespDto.getMcxGrpInd() == 1 ? 1 : null);
                String isPreConfigGroup = corpGrpRespDto.getIsPreConfiguredGroup();
                medGrpRespDetailsDTO.setIsPreConfiguredGroup(isPreConfigGroup == null ? 0 : Integer.parseInt(corpGrpRespDto.getIsPreConfiguredGroup()));
                if (Integer.parseInt(responseDTO.getProtocolVersion().split("\\.")[0]) >= PROTOCOL_VERSION_23) {
                    medGrpRespDetailsDTO.setExternalCorpGroup(com.kodiak.xdms.server.corpmgmt.resources.KnConstants.EXTERNAL_SUBSCRIBER);
                }
                medGrpRespDetailsDTO.setXcapRoot(xcapRootUri);
                medGrpRespDetailsDTOs.add(medGrpRespDetailsDTO);
            }
            corpGrpRespDTO.setAuid(KnConstants.APP_UID_CORP_GROUP);
            corpGrpRespDTO.setAppDetailsList(medGrpRespDetailsDTOs);
            appInfoList.add(corpGrpRespDTO);
        }
        String protocolVersion = responseDTO.getProtocolVersion();
        int protocol = Integer.parseInt(protocolVersion.substring(0, protocolVersion.indexOf('.')));
        if (protocol >= PROTOCOL_VERSION_13) {


            //Auth Doc
            KnAppInfoDTO medAuthRespDTO = new KnAppInfoDTO();
            Collection<KnAppDetailsDTO> medAuthRespDetailsDTOs = new ArrayList<KnAppDetailsDTO>();
            if (responseDTO.getAuthDocDTO() != null){
                KnAppDetailsDTO medAuthRespDetailsDTO = new KnAppDetailsDTO();
                medAuthRespDetailsDTO.setAuid(KnConstants.APP_UID_AUTH_LIST);
                medAuthRespDetailsDTO.setDocName(KnConstants.XCAP_INDEX);
                medAuthRespDetailsDTO.setEtag(responseDTO.getAuthDocDTO().getEtag());
                medAuthRespDetailsDTO.setXcapRoot(xcapRootUri);
                medAuthRespDetailsDTOs.add(medAuthRespDetailsDTO);
            }
            medAuthRespDTO.setAuid(KnConstants.APP_UID_AUTH_LIST);
            medAuthRespDTO.setAppDetailsList(medAuthRespDetailsDTOs);
            appInfoList.add(medAuthRespDTO);


            //Emergency Doc
            KnAppInfoDTO medEmrgRespDTO = new KnAppInfoDTO();
            Collection<KnAppDetailsDTO> medEmrgRespDetailsDTOs = new ArrayList<KnAppDetailsDTO>();
            if(responseDTO.getEmergencyDocDTO() != null){
                KnAppDetailsDTO medEmrgRespDetailsDTO = new KnAppDetailsDTO();
                medEmrgRespDetailsDTO.setAuid(KnConstants.APP_UID_EMERG_CONFIG);
                medEmrgRespDetailsDTO.setDocName(KnConstants.XCAP_INDEX);
                medEmrgRespDetailsDTO.setEtag(responseDTO.getEmergencyDocDTO().getEtag());
                medEmrgRespDetailsDTO.setXcapRoot(xcapRootUri);
                medEmrgRespDetailsDTOs.add(medEmrgRespDetailsDTO);
            }
            medEmrgRespDTO.setAuid(KnConstants.APP_UID_EMERG_CONFIG);
            medEmrgRespDTO.setAppDetailsList(medEmrgRespDetailsDTOs);
            appInfoList.add(medEmrgRespDTO);

            //Group Usage Auid
            KnAppInfoDTO medUsageListRespDto = new KnAppInfoDTO();
            Collection<KnAppDetailsDTO> medUsageListRespDtos = new ArrayList<KnAppDetailsDTO>();
            if(responseDTO.getUsageListDocDTO() != null) {
                KnAppDetailsDTO medUsageListRespDetailsDTO = new KnAppDetailsDTO();
                medUsageListRespDetailsDTO.setAuid(KnConstants.APP_UID_ADDL_TG_LIST);
                medUsageListRespDetailsDTO.setDocName(KnConstants.XCAP_INDEX);
                medUsageListRespDetailsDTO.setEtag(responseDTO.getUsageListDocDTO().getEtag());
                medUsageListRespDetailsDTO.setXcapRoot(xcapRootUri);
                medUsageListRespDtos.add(medUsageListRespDetailsDTO);
            }
            medUsageListRespDto.setAuid(KnConstants.APP_UID_ADDL_TG_LIST);
            medUsageListRespDto.setAppDetailsList(medUsageListRespDtos);
            appInfoList.add(medUsageListRespDto);

        }

        if (protocol >= PROTOCOL_VERSION_14) {

            KnAppInfoDTO tgssRespDTO = new KnAppInfoDTO();
            Collection<KnAppDetailsDTO> tgssListRespDtos = new ArrayList<KnAppDetailsDTO>();
            if(responseDTO.getTgssDocDTO() != null) {
                KnAppDetailsDTO tgssListRespDTO = new KnAppDetailsDTO();
                tgssListRespDTO.setAuid(KnConstants.APP_UID_TGSS_LIST);
                tgssListRespDTO.setDocName(KnConstants.XCAP_INDEX);
                tgssListRespDTO.setEtag(responseDTO.getTgssDocDTO().getEtag());
                tgssListRespDTO.setXcapRoot(xcapRootUri);
                tgssListRespDtos.add(tgssListRespDTO);
            }
            tgssRespDTO.setAuid(KnConstants.APP_UID_TGSS_LIST);
            tgssRespDTO.setAppDetailsList(tgssListRespDtos);
            appInfoList.add(tgssRespDTO);
        }

        medDirRespDTO.setCorpId(responseDTO.getCorpId());
        medDirRespDTO.setUserProfileId(responseDTO.getUserProfileId());
        medDirRespDTO.setXdmsHome(responseDTO.getXdmsHome());
        medDirRespDTO.setAppInfoList(appInfoList);
        medDirRespDTO.setDirEtag(responseDTO.getDirEtag());
        medDirRespDTO.setProtocolVersion(protocolVersion);
        medDirRespDTO.setUserAgent(responseDTO.getUserAgent());
        medDirRespDTO.setBaseMdn(responseDTO.getBaseMdn());
        medDirRespDTO.setActiveFs2(responseDTO.getActiveFS2());
        medDirRespDTO.setSubsriberFS2(responseDTO.getSubscriberFS2());
        medDirRespDTO.setClientType(responseDTO.getClientType());

        knLogger.debug( methodName, "EXIT: ResponseDTO:  ", medDirRespDTO);
        return medDirRespDTO;
    }


    /**
     * @param pubSubsInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnAppInfoDTO getGroupList(KnXDMDirectoryInfoDTO pubSubsInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "getGroupList";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubGroupDTO inputDTO = new KnIPPubGroupDTO();
        inputDTO.setOwner(pubSubsInfoDTO.getMdn());
        inputDTO.setClientType(pubSubsInfoDTO.getClientType());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        Collection<KnPubGroupDTO> responseDTOs = null;
        KnAppInfoDTO medRespDTO = new KnAppInfoDTO();
        medRespDTO.setAuid(KnConstants.APP_UID_PUBLIC_GROUP);

        try {
            responseDTOs = pubClientIntf.getGroupList(inputDTO, persisterTxn);
            knLogger.debug( methodName, "ResponseDTOS:  ", responseDTOs);
            Collection<KnAppDetailsDTO> medRespDetailsDTOs = new ArrayList<KnAppDetailsDTO>();


            for (KnPubGroupDTO responseDTO : responseDTOs) {
                KnAppDetailsDTO medRespDetailsDTO = new KnAppDetailsDTO();
                medRespDetailsDTO.setAuid(KnConstants.APP_UID_PUBLIC_GROUP);
                String docUri = responseDTO.getGroupDocURI();
                knLogger.debug( methodName, "Grp Doc URI:", docUri);
                String grpName = null;
                if (docUri != null && !docUri.equals("")) {
                    grpName = docUri.substring(docUri.lastIndexOf("/") + 1);
                    knLogger.debug( methodName, "Grp Name fetched from Doc URI:", grpName);
                    medRespDetailsDTO.setDocName(grpName);
                } else {
                    knLogger.debug( methodName, "Setting Group Display Name as Grp Name:", responseDTO.getGroupName());
                    medRespDetailsDTO.setDocName(responseDTO.getGroupName());
                }

                medRespDetailsDTO.setEtag(responseDTO.getGroupDocEtag());
                if (null != xcapRootUri && !xcapRootUri.equals("")) {
                    medRespDetailsDTO.setXcapRoot(xcapRootUri);
                } else {
                    medRespDetailsDTO.setXcapRoot(KnConstants.XCAP_ROOT);
                }

                medRespDetailsDTOs.add(medRespDetailsDTO);
            }

            medRespDTO.setAppDetailsList(medRespDetailsDTOs);

            knLogger.debug( methodName, "EXIT: ResponseDTO:  ", medRespDTO);
            return medRespDTO;
        } catch (KnXDMServerException ex) {
            knLogger.error( methodName, "Exception while getting Grp List:  ", ex);
            //return null;
            return medRespDTO;
        }
    }


    /**
     * @param rlsInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMRLSRespDTO getRLSDoc(KnXDMRLSInfoDTO rlsInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "getRLSDoc(rlsInfoDTO, persisterTxn)";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubContactDTO inputDTO = new KnIPPubContactDTO();
        inputDTO.setOwner(rlsInfoDTO.getMdn());
        inputDTO.setClientType(rlsInfoDTO.getClientType());
        inputDTO.setMcpttId(rlsInfoDTO.getMcPttId());
//        inputDTO.setIfMatch(rlsInfoDTO.getIfMatch());
//        inputDTO.setIfNoneMatch(rlsInfoDTO.getIfNoneMatch());
        int rlsDocEtag = rlsInfoDTO.getIfNoneMatch();

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnOPPubDirResponse serverRespDTO = pubClientIntf.getIndexDetails(inputDTO, persisterTxn);

        knLogger.debug( methodName, "ResponseDTO:  ", serverRespDTO);
        xcapRootUri = serverRespDTO.getXcapRootUri();
        int rlsEtag = serverRespDTO.getRlsEtag();

        if (rlsDocEtag > 0 && rlsDocEtag == rlsEtag) {
            knLogger.error( methodName, "RLS Document not modifed :", rlsDocEtag);
            throw new KnXDMServerException(KnMediatorConstants.ERROR_CODE_DOC_NOT_MODIFIED, "RLS not modified");
        }

        KnXDMRLSRespDTO rlsResponseDTO = new KnXDMRLSRespDTO();
        Collection<KnXDMRLSServiceDTO> rlsServiceList = new ArrayList<KnXDMRLSServiceDTO>();
        KnXDMRLSServiceDTO rlsServiceDTO = new KnXDMRLSServiceDTO();

        StringBuffer serviceUri = new StringBuffer(50);
        serviceUri.append("tel:+").append(rlsInfoDTO.getMdn());
        serviceUri.append(";rls-list=").append(KnConstants.XCAP_DEFAULT_CONTACTLIST_NAME);

        rlsServiceDTO.setServiceUri(serviceUri.toString());

        StringBuffer resourceListUri = new StringBuffer(100);

        boolean httpsEnabled = getFeatureBitValue(serverRespDTO.getActiveFS1(), HTTPSSUPPORT.value());
        if (serverRespDTO.getProtocolVersion().matches(KnConstants.PROTOCOL_VERSION_3_0) && httpsEnabled) {
            resourceListUri.append("https://");
        } else {
            resourceListUri.append("http://");
        }

        if (null != xcapRootUri && !xcapRootUri.trim().equals("")) {
            resourceListUri.append(xcapRootUri);
        } else {
            resourceListUri.append(KnConstants.XCAP_ROOT);
        }

        resourceListUri.append("/").append(KnConstants.APP_UID_PUBLIC_CONTACT);
        resourceListUri.append("/users/tel:+").append(rlsInfoDTO.getMdn());
        resourceListUri.append("/").append(KnConstants.APP_UID_PUBLIC_CONTACT);
        resourceListUri.append("/list%5B@name=%22");
        resourceListUri.append(KnConstants.XCAP_DEFAULT_CONTACTLIST_NAME).append("%22%5D");

        rlsServiceDTO.setResourceListUri(resourceListUri.toString());

        String[] packages = new String[1];
        packages[0] = KnConstants.RLS_PACKAGE_NAME_PRESENCE;

        rlsServiceDTO.setPackages(packages);

        rlsServiceList.add(rlsServiceDTO);
        rlsResponseDTO.setServices(rlsServiceList);
        rlsResponseDTO.setRlsEtag(rlsEtag);

        knLogger.debug( methodName, "EXIT: ResponseDTO:  ", rlsResponseDTO);
        return rlsResponseDTO;
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void deleteAllContactsAndGroups(String mdn, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "deleteAllContactsAndGroups";
        knLogger.debug( methodName, "ENTRY : Mdn: ", KnGDPRTemplate.mdn(mdn));
        KnIPPubSubsDTO inputDTO = new KnIPPubSubsDTO();
        inputDTO.setMdn(mdn);
        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        pubClientIntf.deleteAllContactsAndGroups(inputDTO, persisterTxn);
        knLogger.debug( methodName, "Exit : ");
        return;
    }


    /**
     * @param mdn
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void forceSyncPublicData(String mdn, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "forceSyncPublicData";
        knLogger.debug( methodName, "ENTRY : Mdn: ", KnGDPRTemplate.mdn(mdn));
        KnIPPubSubsDTO inputDTO = new KnIPPubSubsDTO();
        inputDTO.setMdn(mdn);
        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        pubClientIntf.forceSync(inputDTO, persisterTxn);
        knLogger.debug( methodName, "Exit : ");
        return;
    }

    /**
     * @param changeMDNInfoDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void changeMdn(KnIPChangeMDNInfoDTO changeMDNInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "changeMdn";
        knLogger.debug( methodName, "ENTRY : ChangeMdnInfoDTO: ", changeMDNInfoDTO);
        pubClientIntf.changeMdn(changeMDNInfoDTO, persisterTxn);
        knLogger.debug( methodName, "Exit : ");
        return;
    }


    /**
     * @param pubGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse createGroup(KnXDMGroupInfoDTO pubGroupInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "createGroup";
        knLogger.debug( methodName, "ENTRY : pubGroupInfoDTO: ", pubGroupInfoDTO);
        KnIPPubGroupInfoDTO inputDTO = new KnIPPubGroupInfoDTO();
        inputDTO.setOwner(pubGroupInfoDTO.getOwnerMdn());
        inputDTO.setGroupDisplayName(pubGroupInfoDTO.getGroupDisplayName());
        inputDTO.setGroupName(pubGroupInfoDTO.getGroupName());
        inputDTO.setGroupType(pubGroupInfoDTO.getGroupType());
        inputDTO.setMcPttId(pubGroupInfoDTO.getMcPttId());
        List<KnGroupMemberDTO> grpMems = new ArrayList<KnGroupMemberDTO>();
        for (KnXDMGroupMdnInfoDTO member : pubGroupInfoDTO.getGrpMembers()) {
            KnGroupMemberDTO listMem = new KnGroupMemberDTO();
            listMem.setMemberMdn(member.getMdn());
            listMem.setMemberName(member.getName());

            grpMems.add(listMem);
        }
        inputDTO.setGroupMembers(grpMems);
        inputDTO.setClientType(pubGroupInfoDTO.getClientType());
        inputDTO.setDocSelectorURI(pubGroupInfoDTO.getDocURI());
        inputDTO.setStrXml(pubGroupInfoDTO.getStrXml());
        inputDTO.setListServiceURI(pubGroupInfoDTO.getListServiceUri());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnOpPubResponse response = pubClientIntf.createGroup(inputDTO, persisterTxn);

        knLogger.debug( methodName, "ResponseDTO:  ", response);
        return response;
    }


    /**
     * @param pubGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse addGroupMembers(KnXDMGroupInfoDTO pubGroupInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "addGroupMembers";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubGroupInfoDTO inputDTO = new KnIPPubGroupInfoDTO();
        inputDTO.setOwner(pubGroupInfoDTO.getOwnerMdn());
        inputDTO.setGroupDisplayName(pubGroupInfoDTO.getGroupDisplayName());
        inputDTO.setGroupName(pubGroupInfoDTO.getGroupName());
        inputDTO.setGroupType(pubGroupInfoDTO.getGroupType());
        inputDTO.setIfMatch(pubGroupInfoDTO.getIfMatch());
        inputDTO.setIfNoneMatch(pubGroupInfoDTO.getIfNoneMatch());
        List<KnGroupMemberDTO> grpMems = new ArrayList<KnGroupMemberDTO>();
        for (KnXDMGroupMdnInfoDTO member : pubGroupInfoDTO.getGrpMembers()) {
            KnGroupMemberDTO listMem = new KnGroupMemberDTO();
            listMem.setMemberMdn(member.getMdn());
            listMem.setMemberName(member.getName());

            grpMems.add(listMem);
        }
        inputDTO.setGroupMembers(grpMems);
        inputDTO.setClientType(pubGroupInfoDTO.getClientType());
        inputDTO.setDocSelectorURI(pubGroupInfoDTO.getDocURI());
        inputDTO.setStrXml(pubGroupInfoDTO.getStrXml());
        inputDTO.setListServiceURI(pubGroupInfoDTO.getListServiceUri());
        inputDTO.setMcPttId(pubGroupInfoDTO.getMcPttId());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnOpPubResponse response = pubClientIntf.addGroupMember(inputDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT: ResponseDTO:  ", response);
        return response;
    }


    /**
     * @param pubGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMPubRespDTO modifyGroupMembers(KnXDMGroupInfoDTO pubGroupInfoDTO, KnPersisterTxn
            persisterTxn) throws KnException {

        String methodName = "modifyGroupMembers";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubGroupInfoDTO inputDTO = new KnIPPubGroupInfoDTO();
        inputDTO.setOwner(pubGroupInfoDTO.getOwnerMdn());
        inputDTO.setGroupDisplayName(pubGroupInfoDTO.getGroupDisplayName());
        inputDTO.setGroupName(pubGroupInfoDTO.getGroupName());
        inputDTO.setGroupType(pubGroupInfoDTO.getGroupType());
        inputDTO.setIfMatch(pubGroupInfoDTO.getIfMatch());
        inputDTO.setIfNoneMatch(pubGroupInfoDTO.getIfNoneMatch());
        inputDTO.setMcPttId(pubGroupInfoDTO.getMcPttId());
        List<KnGroupMemberDTO> grpMems = new ArrayList<KnGroupMemberDTO>();
        for (KnXDMGroupMdnInfoDTO member : pubGroupInfoDTO.getGrpMembers()) {
            KnGroupMemberDTO listMem = new KnGroupMemberDTO();
            listMem.setMemberMdn(member.getMdn());
            listMem.setMemberName(member.getName());

            grpMems.add(listMem);
        }
        inputDTO.setGroupMembers(grpMems);

        Collection<KnMemberDetailsDTO> memDetails = new ArrayList<KnMemberDetailsDTO>();
        for (KnXDMMemberDTO mem : pubGroupInfoDTO.getMemberDetails()) {
            KnMemberDetailsDTO memDetail = new KnMemberDetailsDTO();
            memDetail.setUri(mem.getUri());
            memDetail.setDisplayName(mem.getDisplayName());

            memDetails.add(memDetail);
        }
        inputDTO.setMemberDetails(memDetails);

        inputDTO.setClientType(pubGroupInfoDTO.getClientType());
        inputDTO.setDocSelectorURI(pubGroupInfoDTO.getDocURI());
        inputDTO.setStrXml(pubGroupInfoDTO.getStrXml());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnOpPubResponse response = pubClientIntf.modifyGroupMember(inputDTO, persisterTxn);

        knLogger.debug( methodName, "ResponseDTO:  ", response);

        sendNotification(response.getDirChgDTO(), persisterTxn);
        KnXDMPubRespDTO pubResp = new KnXDMPubRespDTO();

        pubResp.setResponseCode(response.getResponseCode());
        pubResp.setResponseMessage(response.getResponseMessage());
        pubResp.setResponseStatus(response.getResponseStatus());
        pubResp.setDocEtag(response.getDocEtag());

        //Get the XCAP Mobile sync flag from microservice config table.
        boolean xcapMobileSync = commonMediator.getXcapMobileSyncFlag(persisterTxn);
        List<KnPubEXDMSNotifyDTO> grpNotifyDtoList = commonMediator.getPubGrpMicroSrvNotifyDto(response);
        if(xcapMobileSync){
            knLogger.debug(methodName, "Publishing micro service notify");
            commonMediator.startNotifyMicroServicesJob(grpNotifyDtoList);
        }

        knLogger.debug( methodName, "EXIT: ResponseDTO:  ", pubResp);
        return pubResp;
    }


    /**
     * @param pubGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse deleteGroupMembers(KnXDMGroupInfoDTO pubGroupInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "deleteGroupMembers";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubGroupInfoDTO inputDTO = new KnIPPubGroupInfoDTO();
        inputDTO.setOwner(pubGroupInfoDTO.getOwnerMdn());
        inputDTO.setGroupDisplayName(pubGroupInfoDTO.getGroupDisplayName());
        inputDTO.setGroupName(pubGroupInfoDTO.getGroupName());
        inputDTO.setGroupType(pubGroupInfoDTO.getGroupType());
        inputDTO.setIfMatch(pubGroupInfoDTO.getIfMatch());
        inputDTO.setIfNoneMatch(pubGroupInfoDTO.getIfNoneMatch());
        inputDTO.setMcPttId(pubGroupInfoDTO.getMcPttId());
        
        List<KnGroupMemberDTO> grpMems = new ArrayList<KnGroupMemberDTO>();
        for (KnXDMGroupMdnInfoDTO member : pubGroupInfoDTO.getGrpMembers()) {
            KnGroupMemberDTO listMem = new KnGroupMemberDTO();
            listMem.setMemberMdn(member.getMdn());
            listMem.setMemberName(member.getName());

            grpMems.add(listMem);
        }
        inputDTO.setGroupMembers(grpMems);

        Collection<KnMemberDetailsDTO> memDetails = new ArrayList<KnMemberDetailsDTO>();
        for (KnXDMMemberDTO mem : pubGroupInfoDTO.getMemberDetails()) {
            KnMemberDetailsDTO memDetail = new KnMemberDetailsDTO();
            memDetail.setUri(mem.getUri());
            memDetail.setDisplayName(mem.getDisplayName());

            memDetails.add(memDetail);
        }
        inputDTO.setMemberDetails(memDetails);

        inputDTO.setClientType(pubGroupInfoDTO.getClientType());
        inputDTO.setDocSelectorURI(pubGroupInfoDTO.getDocURI());
        inputDTO.setStrXml(pubGroupInfoDTO.getStrXml());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnOpPubResponse response = pubClientIntf.deleteGroupMember(inputDTO, persisterTxn);

            knLogger.debug(methodName, "EXIT: ResponseDTO:  ", response);
        return response;
    }


    /**
     * @param pubGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMPubRespDTO modifyGroupName(KnXDMGroupInfoDTO pubGroupInfoDTO, KnPersisterTxn
            persisterTxn) throws KnException {

        String methodName = "modifyGroupName";
        knLogger.debug( methodName, "ENTRY : ");
        knLogger.debug( methodName, "pubGroupInfoDTO:  ", pubGroupInfoDTO);
        KnIPPubGroupInfoDTO inputDTO = new KnIPPubGroupInfoDTO();
        inputDTO.setOwner(pubGroupInfoDTO.getOwnerMdn());
        inputDTO.setGroupDisplayName(pubGroupInfoDTO.getGroupDisplayName());
        inputDTO.setGroupName(pubGroupInfoDTO.getGroupName());
        inputDTO.setGroupType(pubGroupInfoDTO.getGroupType());
        inputDTO.setIfMatch(pubGroupInfoDTO.getIfMatch());
        inputDTO.setIfNoneMatch(pubGroupInfoDTO.getIfNoneMatch());
        inputDTO.setClientType(pubGroupInfoDTO.getClientType());
        inputDTO.setDocSelectorURI(pubGroupInfoDTO.getDocURI());
        inputDTO.setStrXml(pubGroupInfoDTO.getStrXml());
        inputDTO.setMcPttId(pubGroupInfoDTO.getMcPttId());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnOpPubResponse response = pubClientIntf.modifyGroupName(inputDTO, persisterTxn);

        knLogger.debug( methodName, "ResponseDTO:  ", response);

        sendNotification(response.getDirChgDTO(), persisterTxn);
        KnXDMPubRespDTO pubResp = new KnXDMPubRespDTO();

        pubResp.setResponseCode(response.getResponseCode());
        pubResp.setResponseMessage(response.getResponseMessage());
        pubResp.setResponseStatus(response.getResponseStatus());
        pubResp.setDocEtag(response.getDocEtag());
        /*//Get the XCAP Mobile sync flag from microservice config table.
        boolean xcapMobileSync = commonMediator.getXcapMobileSyncFlag(persisterTxn);
        //Get the RMQ configurations
        KnMqServiceConfig rmqInfoDto = commonMediator.retrieveServerConfDetails(persisterTxn);
        knLogger.debug(methodName, "xcapMobileSync - " , xcapMobileSync, " rmqInfoDto - ", rmqInfoDto);
        List<KnPubEXDMSNotifyDTO> grpNotifyDtoList = commonMediator.getPubGrpMicroSrvNotifyDto(response);
        if(xcapMobileSync){
            knLogger.debug(methodName, "Publishing micro service notify");
            commonMediator.startNotifyMicroServicesJob(grpNotifyDtoList, rmqInfoDto);
        }*/

        knLogger.debug( methodName, "EXIT: ResponseDTO:  ", pubResp);
        return pubResp;
    }


    /**
     * @param pubGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse deleteGroup(KnXDMGroupInfoDTO pubGroupInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "deleteGroup";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubGroupInfoDTO inputDTO = new KnIPPubGroupInfoDTO();
        inputDTO.setOwner(pubGroupInfoDTO.getOwnerMdn());
        inputDTO.setGroupDisplayName(pubGroupInfoDTO.getGroupDisplayName());
        inputDTO.setGroupName(pubGroupInfoDTO.getGroupName());
        inputDTO.setGroupType(pubGroupInfoDTO.getGroupType());
        inputDTO.setIfMatch(pubGroupInfoDTO.getIfMatch());
        inputDTO.setIfNoneMatch(pubGroupInfoDTO.getIfNoneMatch());
        inputDTO.setClientType(pubGroupInfoDTO.getClientType());
        inputDTO.setDocSelectorURI(pubGroupInfoDTO.getDocURI());
        inputDTO.setMcPttId(pubGroupInfoDTO.getMcPttId());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        knLogger.debug( methodName, "Library Call : DELETEMCPTTID:  ", KnGDPRTemplate.mcpttId(inputDTO.getMcPttId()));
        KnOpPubResponse response = pubClientIntf.deleteGroup(inputDTO, persisterTxn);

               knLogger.debug(methodName, "EXIT: ResponseDTO:  ", response);
        return response;
    }


    /**
     * @param pubGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMGroupInfoRespDTO getGroupDetails(KnXDMGroupInfoDTO pubGroupInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "getGroupDetails";
        knLogger.debug( methodName, "ENTRY : ");
        knLogger.debug( methodName, "MCPTTID : ",KnGDPRTemplate.mcpttId(pubGroupInfoDTO.getMcPttId()));
        KnIPPubGroupInfoDTO inputDTO = new KnIPPubGroupInfoDTO();
        inputDTO.setOwner(pubGroupInfoDTO.getOwnerMdn());
        inputDTO.setGroupDisplayName(pubGroupInfoDTO.getGroupDisplayName());
        inputDTO.setGroupName(pubGroupInfoDTO.getGroupName());
        inputDTO.setGroupType(pubGroupInfoDTO.getGroupType());
        inputDTO.setClientType(pubGroupInfoDTO.getClientType());
        inputDTO.setGroupDocURI(pubGroupInfoDTO.getDocURI());
        inputDTO.setIfMatch(pubGroupInfoDTO.getIfMatch());
        inputDTO.setIfNoneMatch(pubGroupInfoDTO.getIfNoneMatch());
        inputDTO.setMcPttId(pubGroupInfoDTO.getMcPttId());;

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnPubGroupInfoDTO response = pubClientIntf.getGroupDocDetails(inputDTO, persisterTxn);
        KnXDMGroupInfoRespDTO pubResp = new KnXDMGroupInfoRespDTO();

        pubResp.setGroupName(response.getGroupName());
        pubResp.setGroupDisplayName(response.getGroupDisplayName());
        pubResp.setDocURI(response.getDocSelectorURI());

        pubResp.setGroupType(response.getGroupType());
        pubResp.setStrXml(response.getStrXml());
        pubResp.setEtag(response.getGroupDocEtag());

        knLogger.debug( methodName, "EXIT: ResponseDTO:  ", pubResp);
        return pubResp;
    }

    /**
     * @param pubGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMGroupDetailsRespDTO getPubGroupDetails(KnXDMGroupInfoDTO pubGroupInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "getGroupDetails";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubGroupInfoDTO inputDTO = new KnIPPubGroupInfoDTO();
        inputDTO.setOwner(pubGroupInfoDTO.getOwnerMdn());
        inputDTO.setGroupDisplayName(pubGroupInfoDTO.getGroupDisplayName());
        inputDTO.setGroupName(pubGroupInfoDTO.getGroupName());
        inputDTO.setMcPttId(pubGroupInfoDTO.getMcPttId());
        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnPubGroupInfoDTO response = pubClientIntf.getPubGroupDetails(inputDTO, persisterTxn);;
        // Populate the details response
        KnXDMGroupDetailsRespDTO xdmRespDTO = new KnXDMGroupDetailsRespDTO();
        xdmRespDTO.setGroupName(response.getGroupName());
        xdmRespDTO.setGroupDisplayName(response.getGroupDisplayName());
        xdmRespDTO.setEtag(response.getGroupDocEtag());
        xdmRespDTO.setGroupType(response.getGroupType());
        xdmRespDTO.setOwnerMdn(response.getOwner());
        xdmRespDTO.setMemberCount(response.getGroupMemberCount());
        xdmRespDTO.setDocURI(response.getDocSelectorURI());
        xdmRespDTO.setListServiceUri(response.getListServiceURI());
        KnXDMGroupMdnInfoDTO grpMemberInfo = null;
        Collection<KnGroupMemberDTO> grpMembersList = response.getGroupMembers();
        ArrayList<KnXDMGroupMdnInfoDTO> groupMdnList = new ArrayList<KnXDMGroupMdnInfoDTO>();
        if (null != grpMembersList && !grpMembersList.isEmpty()) {
            for (KnGroupMemberDTO grpMember : grpMembersList) {
                grpMemberInfo = new KnXDMGroupMdnInfoDTO();
                grpMemberInfo.setMdn(grpMember.getMemberMdn());
                grpMemberInfo.setName(grpMember.getMemberName());
                grpMemberInfo.setUserId(grpMember.getUserId());
                grpMemberInfo.setAliasMdn(grpMember.getAliasMdn());
                grpMemberInfo.setUfmi(grpMember.getUfmi());
                groupMdnList.add(grpMemberInfo);
            }
        }
        xdmRespDTO.setGrpMembers(groupMdnList);
        knLogger.debug( methodName, "EXIT: ResponseDTO:  ", xdmRespDTO);
        return xdmRespDTO;
    }

    /**
     * @param dirChgDTO
     * @param persisterTxn
     * @return
     */
    private boolean sendNotification(KnOPDirChgDTO dirChgDTO, KnPersisterTxn persisterTxn) {

        String methodName = "sendNotification";

        // Populate the KnXcapDiffNotifyDTO
        // Send the notification
        knLogger.debug( methodName, "Populating the Notificaton: DTO : ", dirChgDTO);
        KnXcapDiffNotifyDTO xcapDiffNotifyDTO = new KnXcapDiffNotifyDTO();

        Collection<KnXcapDiffDocDTO> diffDocList = new ArrayList<KnXcapDiffDocDTO>();
        Collection<KnOPDocChgDTO> docChgList = dirChgDTO.getDocChgDTO();

        for (KnOPDocChgDTO docChgDTO : docChgList) {
            KnXcapDiffDocDTO xcapDiffDocDTO = new KnXcapDiffDocDTO();
            xcapDiffDocDTO.setDocChangeType(docChgDTO.getDocumentChgType());
            xcapDiffDocDTO.setDocEtag(docChgDTO.getNewEtag());
            xcapDiffDocDTO.setDocumentSelector(docChgDTO.getDocUri());
            xcapDiffDocDTO.setDocUri(docChgDTO.getEntryUri());
            xcapDiffDocDTO.setVideoPermission(docChgDTO.getVideoPermission());
            diffDocList.add(xcapDiffDocDTO);
        }

        xcapDiffNotifyDTO.setDocDiffObj(diffDocList);
        xcapDiffNotifyDTO.setDirNewEtag(dirChgDTO.getDirNewEtag());
        xcapDiffNotifyDTO.setDirPrevEtag(dirChgDTO.getDirPrevEtag());
        xcapDiffNotifyDTO.setDirURI(dirChgDTO.getDirUri());
        xcapDiffNotifyDTO.setPocHome(dirChgDTO.getPocHome());
        xcapDiffNotifyDTO.setPresenceHome(dirChgDTO.getPresenceHome());
        xcapDiffNotifyDTO.setNtfyOnAnyMDN(dirChgDTO.getNtfyOnAnyMDN());
        knLogger.debug( methodName, "Getting the Notifier Instance:");
        KnXcapDiffNotifier xcapDiffNotifier = KnXcapDiffNotifier.getInstance();
        knLogger.debug( methodName, "Sending the Notificaton: Diff DTO : ", xcapDiffNotifyDTO);
        boolean notificationStatus = notifier.sendXcapDiffNotifications(xcapDiffNotifyDTO);
        knLogger.debug( methodName, "Notificaton Status :", notificationStatus);

        return notificationStatus;
    }

    private boolean getFeatureBitValue(long clientFeatureSet, int bitNumber) {
        String methodName = "getFeatureBit(long,int )";
        knLogger.debug( methodName, "ENTRY: get Feature Bit data ");
        BitSet bitSet = convertLongToBitSet(clientFeatureSet);
        boolean bitValue = bitSet.get(bitNumber);
        knLogger.debug( methodName, "EXIT: Feature Bit Value - ", bitValue);
        return bitValue;
    }

    public BitSet convertLongToBitSet(long longValue) {
        String methodName = "convertLongToBitSet(long)";
        knLogger.debug( methodName, "ENTRY: Received long value to convert bit set is - ", longValue);

        long value = longValue;

        BitSet bitSet = new BitSet(Long.SIZE);
        int index = 0;
        while (value != 0) {
            if (value % 2L != 0) {
                bitSet.set(index);
            }
            ++index;
            value = value >>> 1;
        }
        knLogger.debug( methodName, "EXIT: Generated BitSet - ", bitSet);

        return bitSet;

    }


    public KnXDMScanlistResponseDTO updateScanList(KnXDMScanlistInfoDTO scanListInfoDTO, KnXDMTalkGroupServerRespDTO corpGetResp , long corpId, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateScanList(KnXDMScanlistInfoDTO,KnXDMTalkGroupServerRespDTO, corpId,KnPersisterTxn)";
        knLogger.entry(methodName, scanListInfoDTO, corpId, corpGetResp);
        KnXDMScanlistResponseDTO responseDTO = new KnXDMScanlistResponseDTO();
        //Converting from Array to List
        KnIPTalkGroupDTO ipTalkGroupDTO = getFilteredScanList(scanListInfoDTO.getScanList(), corpGetResp.getCampGrpList());
        ipTalkGroupDTO.setMdn(scanListInfoDTO.getMdn());
        ipTalkGroupDTO.setCorpId(String.valueOf(corpId));
        ipTalkGroupDTO.setEtag(scanListInfoDTO.getEtag());
        ipTalkGroupDTO.setClientType(scanListInfoDTO.getClientType());
        ipTalkGroupDTO.setOperationType(scanListInfoDTO.getOperationType());
        ipTalkGroupDTO.setHierarchyType(scanListInfoDTO.getHierarchyType());
        ipTalkGroupDTO.setMcPttId(scanListInfoDTO.getMcPttId());
        knLogger.info(methodName, "Calling corp library for modify Subscriber Scan List");
        KnCorpResponseDTO respDto = corpClientIntf.modifySubscriberScanListXcapClients(ipTalkGroupDTO, persisterTxn);
        knLogger.info(methodName, "Returned from corporate library");

        int status = respDto.getStatus();
        responseDTO.setResponseStatus(status);
        responseDTO.setResponseCode(respDto.getStatusCode());
        responseDTO.setResponseMessage(respDto.getMessage());
        if (KnConstants.RESPONSE_STATUS.SUCCESS.value() == respDto.getStatus()) {
             responseDTO.setEtag(Long.parseLong(respDto.getEtag()));
            //commonMediator.sendTGSModeChangeNotification(respDto.getTgsModeChgMap());
        }
        knLogger.exit(methodName, responseDTO.getEtag());
        return responseDTO;
    }

    private KnIPTalkGroupDTO getFilteredScanList(List<KnXDMTalkGroupInfoDTO> reqList, List<KnXDMTalkGroupInfoDTO> dbList){
        String methodName = "getFilteredScanList(List<KnXDMTalkGroupInfoDTO>,List<KnXDMTalkGroupInfoDTO>)";
        knLogger.entry(methodName, reqList,dbList);
        KnIPTalkGroupDTO respObj = new KnIPTalkGroupDTO();
        Map<Integer, Integer> reqGrpMap = new HashMap<>(reqList.size());
        Map<Integer, Integer> existingGrpMap = new HashMap<>(dbList.size());
        List<Integer> modifiedList = new ArrayList<>();
        for(KnXDMTalkGroupInfoDTO dto : dbList){
            existingGrpMap.put(dto.getGroupId(), dto.getPriority());
        }
        List<Integer> exitList = new ArrayList<Integer>();
        knLogger.debug(methodName, "ScanList exits in DB putting into map ", existingGrpMap);
        for(KnXDMTalkGroupInfoDTO dto : reqList){
            reqGrpMap.put(dto.getGroupId(), dto.getPriority());
            if(existingGrpMap.get(dto.getGroupId()) != null){
                int dbPri = existingGrpMap.get(dto.getGroupId());
                if(dbPri != dto.getPriority()){
                    modifiedList.add(dto.getGroupId());
                } else{
                    exitList.add(dto.getGroupId());
                }
            }
        }
        knLogger.debug(methodName, "Modified List", modifiedList);
        List<Integer> reqList1 = new ArrayList<>(reqGrpMap.keySet());
        List<Integer> dbList1 = new ArrayList<>(existingGrpMap.keySet());
        List<Integer> addedList = new ArrayList<>(reqList1);
        List<Integer> removedList = new ArrayList<>(dbList1);
        addedList.removeAll(dbList1);
        addedList.addAll(exitList);
        removedList.removeAll(reqList1);
        knLogger.debug(methodName, "Added List ", addedList);
        knLogger.debug(methodName, "Removed List ", removedList);
        List<KnXDMTalkGroupInfoDTO> addedDtoList = new ArrayList<>(addedList.size());
        for(int id : addedList){
            KnXDMTalkGroupInfoDTO dto = new KnXDMTalkGroupInfoDTO();
            dto.setGroupId(id);
            dto.setPriority(reqGrpMap.get(id));
            addedDtoList.add(dto);
        }
        respObj.setAddedCampGrpList(addedDtoList);
        List<KnXDMTalkGroupInfoDTO> removedDtoList = new ArrayList<>(removedList.size());
        for(int id : removedList){
            KnXDMTalkGroupInfoDTO dto = new KnXDMTalkGroupInfoDTO();
            dto.setGroupId(id);
            removedDtoList.add(dto);
        }
        respObj.setRemovedCammpGrpList(removedDtoList);
        List<KnXDMTalkGroupInfoDTO> modifiedDtoList = new ArrayList<>(modifiedList.size());
        for(int id : modifiedList){
            KnXDMTalkGroupInfoDTO dto = new KnXDMTalkGroupInfoDTO();
            dto.setGroupId(id);
            dto.setPriority(reqGrpMap.get(id));
            modifiedDtoList.add(dto);
        }
        respObj.setModifiedCampGrpList(modifiedDtoList);
        knLogger.exit(methodName, respObj);
        return respObj;
    }

    public KnXDMScanlistResponseDTO getScanList(KnXDMScanlistInfoDTO scanListInfoDTO, long corpId, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getScanList(KnXDMScanlistInfoDTO, KnPersisterTxn)";
        KnXDMScanlistResponseDTO respDto = new KnXDMScanlistResponseDTO();
        knLogger.entry(methodName, scanListInfoDTO,corpId);
        KnIPTalkGroupDTO ipTalkGroupDTO = new KnIPTalkGroupDTO();
        ipTalkGroupDTO.setMdn(scanListInfoDTO.getMdn());
        ipTalkGroupDTO.setEtag(scanListInfoDTO.getEtag());
        ipTalkGroupDTO.setOperationType(scanListInfoDTO.getOperationType());
        ipTalkGroupDTO.setClientType(scanListInfoDTO.getClientType());
        ipTalkGroupDTO.setCorpId(String.valueOf(corpId));
        ipTalkGroupDTO.setHierarchyType(scanListInfoDTO.getHierarchyType());
        ipTalkGroupDTO.setMcPttId(scanListInfoDTO.getMcPttId());
        
        knLogger.info(methodName, "Calling corp library for getSubscriberScanList");
        KnXDMTalkGroupServerRespDTO xdmRespDto = corpClientIntf.getSubscriberScanListXcap(ipTalkGroupDTO, persisterTxn);
        knLogger.info(methodName, "Returned from corporate library ");

        int status = xdmRespDto.getStatus();
        respDto.setResponseStatus(status);
        respDto.setResponseCode(xdmRespDto.getStatusCode());
        respDto.setResponseMessage(xdmRespDto.getMessage());

        if (KnConstants.RESPONSE_STATUS.SUCCESS.value() == xdmRespDto.getStatus() && xdmRespDto.getCampGrpList().size()!=0) {
            respDto.setCorpId(corpId);
            respDto.setScanList(xdmRespDto.getCampGrpList());
            respDto.setEnabled(xdmRespDto.getMode());
            respDto.setCampModeCap(xdmRespDto.getCampModeCap());
            respDto.setEtag(Long.parseLong(xdmRespDto.getEtag()));
        }
        knLogger.exit(methodName, respDto);
        return respDto;
    }

    public KnXDMScanlistResponseDTO deleteSubscriberScanList(KnXDMScanlistInfoDTO scanListInfoDTO, long corpId, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        String methodName = "deleteSubscriberScanList(KnXDMScanlistInfoDTO, KnPersisterTxn)";
        knLogger.entry(methodName, scanListInfoDTO,corpId);
        KnXDMScanlistResponseDTO respDto = new KnXDMScanlistResponseDTO();

        KnIPTalkGroupDTO ipTalkGroupDTO = new KnIPTalkGroupDTO();
        ipTalkGroupDTO.setMdn(scanListInfoDTO.getMdn());
        ipTalkGroupDTO.setEtag(scanListInfoDTO.getEtag());
        ipTalkGroupDTO.setOperationType(scanListInfoDTO.getOperationType());
        ipTalkGroupDTO.setClientType(scanListInfoDTO.getClientType());
        ipTalkGroupDTO.setCorpId(String.valueOf(corpId));
        ipTalkGroupDTO.setMcPttId(scanListInfoDTO.getMcPttId());
        knLogger.info(methodName, "Calling corp library for delete Subscriber ScanList ");
        KnCorpResponseDTO  xdmRespDto = corpClientIntf.deleteSubscriberScanList(ipTalkGroupDTO, persisterTxn);
        knLogger.info(methodName, "Returned from corporate library");

        int status = xdmRespDto.getStatus();
        respDto.setResponseStatus(status);
        respDto.setResponseCode(xdmRespDto.getStatusCode());
        respDto.setResponseMessage(xdmRespDto.getMessage());
        if (KnConstants.RESPONSE_STATUS.SUCCESS.value() == xdmRespDto.getStatus()) {
            //commonMediator.sendTGSModeChangeNotification(xdmRespDto.getTgsModeChgMap());
            knLogger.info(methodName, "Returning Success response after deleting ScanList"+respDto.getScanList());
        }
        knLogger.exit(methodName, respDto);
        return respDto;
    }

    public KnOpPubResponse createNonSharedGroup(KnXDMGroupInfoDTO pubGroupInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException{
        String methodName = "createNonSharedGroup";
        knLogger.debug( methodName, "ENTRY : pubGroupInfoDTO: ", pubGroupInfoDTO);
        KnIPPubGroupInfoDTO inputDTO = new KnIPPubGroupInfoDTO();
        inputDTO.setOwner(pubGroupInfoDTO.getOwnerMdn());
        inputDTO.setGroupDisplayName(pubGroupInfoDTO.getGroupDisplayName());
        inputDTO.setGroupName(pubGroupInfoDTO.getGroupName());
        inputDTO.setGroupType(pubGroupInfoDTO.getGroupType());
        List<KnGroupMemberDTO> grpMems = new ArrayList<KnGroupMemberDTO>();
        for (KnXDMGroupMdnInfoDTO member : pubGroupInfoDTO.getGrpMembers()) {
            KnGroupMemberDTO listMem = new KnGroupMemberDTO();
            listMem.setMemberMdn(member.getMdn());
            listMem.setMemberName(member.getName());

            grpMems.add(listMem);
        }
        inputDTO.setVendorId(pubGroupInfoDTO.getVendorID());
        inputDTO.setGroupMembers(grpMems);
        inputDTO.setClientType(pubGroupInfoDTO.getClientType());
        inputDTO.setStrXml(pubGroupInfoDTO.getStrXml());
        inputDTO.setGroupType(pubGroupInfoDTO.getGroupType());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnOpPubResponse response = pubClientIntf.createNonSharedGroup(inputDTO, persisterTxn);

        knLogger.debug( methodName, "ResponseDTO:  ", response);
        return response;
    }

    /**
     * Method to modify (add/update/remove) dynamic contact for third party clients.
     * @param contactDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse modifyDynamicContacts(KnXDMDynContactInfoDTO contactDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "modifyDynamicContacts";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubContactInfoDTO inputDTO = new KnIPPubContactInfoDTO();
        inputDTO.setOwner(contactDTO.getOwnerMdn());
        inputDTO.setVendorId(contactDTO.getVendorId());

        if(contactDTO.getContactsList() != null){
            List<KnMemberDTO> addedContList = new ArrayList<KnMemberDTO>();
            for (KnXDMMdnInfoDTO member : contactDTO.getContactsList()) {
                KnMemberDTO listMem = new KnMemberDTO();
                listMem.setMemberMdn(member.getMdn());
                listMem.setMemberName(member.getName());

                addedContList.add(listMem);
            }
            inputDTO.setMembers(addedContList);
        }

        if(contactDTO.getModifiedContList() != null){
            List<KnMemberDTO> modifiedContList = new ArrayList<>();
            for (KnXDMMdnInfoDTO member : contactDTO.getModifiedContList()) {
                KnMemberDTO listMem = new KnMemberDTO();
                listMem.setMemberMdn(member.getMdn());
                listMem.setMemberName(member.getName());
                modifiedContList.add(listMem);
            }
            inputDTO.setModifiedContList(modifiedContList);
        }

        inputDTO.setRemovedContList(contactDTO.getRemovedContList());

        inputDTO.setClientType(contactDTO.getClientType());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnOpPubResponse response;
        response = pubClientIntf.modifyDynamicContacts(inputDTO, persisterTxn);
        return response;
    }

    public KnPubContactDTO deleteDynamicContacts(KnXDMDynContactInfoDTO contactDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException{
        String methodName = "deleteDynamicContacts";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubContactInfoDTO inputDTO = new KnIPPubContactInfoDTO();
        inputDTO.setOwner(contactDTO.getOwnerMdn());
        inputDTO.setClientType(contactDTO.getClientType());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnPubContactDTO response = pubClientIntf.deleteDynamicContacts(inputDTO, persisterTxn);
        knLogger.debug( methodName, "Response - ", response);
        return response;
    }


    public KnPubContactDTO getDynamicContacts(KnXDMDynContactInfoDTO contactDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException{
        String methodName = "getDynamicContacts";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubContactInfoDTO inputDTO = new KnIPPubContactInfoDTO();
        inputDTO.setOwner(contactDTO.getOwnerMdn());
        inputDTO.setVendorId(contactDTO.getVendorId());
        inputDTO.setClientType(contactDTO.getClientType());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnPubContactDTO response = pubClientIntf.getDynamicContacts(inputDTO, persisterTxn);
        knLogger.debug( methodName, "Response - ", response);
        return response;
    }


    /**
     * Interface to retrieve the dynamic group details.
     * @param pubGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */

    public KnXDMGroupDetailsRespDTO getDynamicNonSharedGrpDetails(KnXDMGroupInfoDTO pubGroupInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "getDynamicGroupDetails";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubGroupInfoDTO inputDTO = new KnIPPubGroupInfoDTO();
        inputDTO.setOwner(pubGroupInfoDTO.getOwnerMdn());
        inputDTO.setGroupDisplayName(pubGroupInfoDTO.getGroupDisplayName());
        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnPubGroupInfoDTO response = pubClientIntf.getDynamicGroupDetails(inputDTO, persisterTxn);
        KnXDMGroupDetailsRespDTO pubResp = new KnXDMGroupDetailsRespDTO();
        pubResp.setGroupDisplayName(response.getGroupDisplayName());
        pubResp.setOwnerMdn(response.getOwner());
        pubResp.setGroupType(response.getGroupType());
        List<KnXDMGroupMdnInfoDTO> memList = new ArrayList<>();
        if(response.getGroupMembers() != null){
            for(KnGroupMemberDTO member : response.getGroupMembers()){
                KnXDMGroupMdnInfoDTO grpMdnInfoDTO = new KnXDMGroupMdnInfoDTO();
                grpMdnInfoDTO.setMdn(member.getMemberMdn());
                grpMdnInfoDTO.setName(member.getMemberName());
                memList.add(grpMdnInfoDTO);
            }
        }
        pubResp.setGrpMembers(memList);

        knLogger.debug( methodName, "EXIT: ResponseDTO:  ", pubResp);
        return pubResp;
    }


    public KnXDMGroupListRespDTO getDynamicNonSharedGrpList(KnXDMGroupInfoDTO groupInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException{
        String methodName = "getDynamicGroupDetails";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubGroupInfoDTO inputDTO = new KnIPPubGroupInfoDTO();
        inputDTO.setOwner(groupInfoDTO.getOwnerMdn());
        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        List<KnPubGroupDTO> groupList = pubClientIntf.getDynamicNonSharedGrpList(inputDTO, persisterTxn);
        KnXDMGroupListRespDTO pubResp = new KnXDMGroupListRespDTO();
        List<KnXDMGroupDTO> groups = new ArrayList<>();
        if(groupList != null ){
            for(KnPubGroupDTO pubGroupDTO : groupList){
                KnXDMGroupDTO grp = new KnXDMGroupDetailDTO();
                grp.setGroupDisplayName(pubGroupDTO.getGroupName());
                grp.setGroupType(pubGroupDTO.getGroupType());
                groups.add(grp);
            }
            pubResp.setGroupList(groups);
        }

        knLogger.debug( methodName, "EXIT: ResponseDTO:  ", pubResp);
        return pubResp;
    }

    public KnOpPubResponse deleteDynamicNonSharedGrp(KnXDMGroupInfoDTO pubGroupInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException{
        String methodName = "deleteDynamicNonSharedGrp";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubGroupInfoDTO inputDTO = new KnIPPubGroupInfoDTO();
        inputDTO.setOwner(pubGroupInfoDTO.getOwnerMdn());
        inputDTO.setGroupDisplayName(pubGroupInfoDTO.getGroupDisplayName());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnOpPubResponse response = pubClientIntf.deleteDynamicNonSharedGrp(inputDTO, persisterTxn);

        knLogger.debug(methodName, "EXIT: ResponseDTO:  ", response);
        return response;
    }

    /**
     * Interface to modify non-shared group details - Add/modify/remove members are supported
     * @param pubGroupInfoDTO
     * @param persisterTxn
     * @return
     */
    public KnOpPubResponse modifyNonSharedGroup(KnXDMGroupInfoDTO pubGroupInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException{
        String methodName = "modifyNonSharedGroup";
        knLogger.debug( methodName, "ENTRY : pubGroupInfoDTO: ", pubGroupInfoDTO);
        KnIPPubGroupInfoDTO inputDTO = new KnIPPubGroupInfoDTO();
        inputDTO.setOwner(pubGroupInfoDTO.getOwnerMdn());
        inputDTO.setGroupDisplayName(pubGroupInfoDTO.getGroupDisplayName());
        inputDTO.setGroupName(pubGroupInfoDTO.getGroupName());
        inputDTO.setVendorId(pubGroupInfoDTO.getVendorID());
        inputDTO.setNewGrpName(pubGroupInfoDTO.getNewGrpName());
        inputDTO.setRemovedMembers(pubGroupInfoDTO.getRemovenMembers());
        List<KnGroupMemberDTO> grpMems = new ArrayList<KnGroupMemberDTO>();
        for (KnXDMGroupMdnInfoDTO member : pubGroupInfoDTO.getGrpMembers()) {
            KnGroupMemberDTO listMem = new KnGroupMemberDTO();
            listMem.setMemberMdn(member.getMdn());
            listMem.setMemberName(member.getName());

            grpMems.add(listMem);
        }
        inputDTO.setGroupMembers(grpMems);

        List<KnGroupMemberDTO> modiMems = new ArrayList<KnGroupMemberDTO>();
        for (KnXDMGroupMdnInfoDTO member : pubGroupInfoDTO.getModifiedMembers()) {
            KnGroupMemberDTO listMem = new KnGroupMemberDTO();
            listMem.setMemberMdn(member.getMdn());
            listMem.setMemberName(member.getName());

            modiMems.add(listMem);
        }
        inputDTO.setModifiedMembers(modiMems);

        // inputDTO.setDocSelectorURI(pubGroupInfoDTO.getDocURI());
        //inputDTO.setStrXml(pubGroupInfoDTO.getStrXml());
        //inputDTO.setListServiceURI(pubGroupInfoDTO.getListServiceUri());

        knLogger.debug( methodName, "Library Call : InputDTO:  ", inputDTO);
        KnOpPubResponse response = pubClientIntf.modifyNonSharedGroup(inputDTO, persisterTxn);

        knLogger.debug( methodName, "ResponseDTO:  ", response);
        return response;
    }

    public KnXDMAuthListResponseDTO getAuthorizationList(KnXDMAuthListRequestDTO authListRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getAuthorizationList(KnXDMAuthListRequestDTO, KnPersisterTxn)";
        KnXDMAuthListResponseDTO respDto = new KnXDMAuthListResponseDTO();
        knLogger.info(methodName, "input dto ",authListRequestDTO);
        KnIPPubAuthListDTO ipPubAuthListDTO = new KnIPPubAuthListDTO();
        ipPubAuthListDTO.setAuthMDN(authListRequestDTO.getAuthMdn());
        ipPubAuthListDTO.setIfMatch(authListRequestDTO.getIfMatch());
        ipPubAuthListDTO.setIfNoneMatch(authListRequestDTO.getIfNoneMatch());
        ipPubAuthListDTO.setMcpttId(authListRequestDTO.getMcpttId());
        knLogger.info(methodName, "Calling public library for getAuthorizationList");
        KnIPPubAuthListDTO pubAuthListDTO = pubClientIntf.getAuthorizationList(ipPubAuthListDTO, persisterTxn);;
        knLogger.info(methodName, "Returned from public  library ");
        respDto.setAuthMdn(pubAuthListDTO.getAuthMDN());
        List<KnTargetMdnInfoDTO> targetMdnInfoDTOS = new ArrayList<>();
        if(pubAuthListDTO.getTargetMDNInfoDTOS() != null) {
            for (KnTargetMDNInfoDTO dto : pubAuthListDTO.getTargetMDNInfoDTOS()) {
                KnTargetMdnInfoDTO targetMdnInfoDTO = new KnTargetMdnInfoDTO();
                targetMdnInfoDTO.setTargetMdn(dto.getTargetMdn());
                targetMdnInfoDTO.setFeaturePermission(dto.getFeaturePermissions());
                targetMdnInfoDTO.setFeatureStatus(dto.getFeatureStatus());
                targetMdnInfoDTOS.add(targetMdnInfoDTO);
            }
        }
        respDto.setTargetInfo(targetMdnInfoDTOS);
        respDto.setDocEtag(pubAuthListDTO.getDocEtag());
        knLogger.info(methodName, "response from pub mediator", respDto);
        return respDto;
    }

    public KnOpPubResponse updateAuthList(KnXDMAuthListRequestDTO authListRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "updateAuthList(KnXDMAuthListRequestDTO, KnPersisterTxn)";
        knLogger.info(methodName, authListRequestDTO);

        //Converting from Array to List
        KnIPPubAuthListDTO ipPubAuthListDTO = new KnIPPubAuthListDTO();
        ipPubAuthListDTO.setAuthMDN(authListRequestDTO.getAuthMdn());
        List<KnTargetMDNInfoDTO> targetMDNInfoDTOS = new ArrayList<>();
        for(KnTargetMdnInfoDTO infoDTO : authListRequestDTO.getTargetInfo()){
            KnTargetMDNInfoDTO dto = new KnTargetMDNInfoDTO();
            dto.setTargetMdn(infoDTO.getTargetMdn());
            dto.setFeatureStatus(infoDTO.getFeatureStatus());
            targetMDNInfoDTOS.add(dto);
        }
        ipPubAuthListDTO.setTargetMDNInfoDTOS(targetMDNInfoDTOS);
        ipPubAuthListDTO.setIfMatch(authListRequestDTO.getEtag());
        ipPubAuthListDTO.setMcpttId(authListRequestDTO.getMcpttId());
        knLogger.info(methodName, "Calling public library for update ");
        KnOpPubResponse pubAuthListDTO = pubClientIntf.updateAuthorizationList(ipPubAuthListDTO, persisterTxn);;
        knLogger.info(methodName, "Returned from public  library ");
        return pubAuthListDTO;
    }

    public KnXDMEmergencyConfigResponseDTO getEmergencyConfigDoc(KnXDMAuthListRequestDTO authListRequestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getEmergencyConfigDoc(KnXDMAuthListRequestDTO, KnPersisterTxn)";
        KnXDMEmergencyConfigResponseDTO respDto = new KnXDMEmergencyConfigResponseDTO();
        knLogger.info(methodName, "input dto ",authListRequestDTO);
        KnIPPubAuthListDTO ipPubAuthListDTO = new KnIPPubAuthListDTO();
        ipPubAuthListDTO.setAuthMDN(authListRequestDTO.getAuthMdn());
        ipPubAuthListDTO.setIfMatch(authListRequestDTO.getIfMatch());
        ipPubAuthListDTO.setIfNoneMatch(authListRequestDTO.getIfNoneMatch());
        ipPubAuthListDTO.setMcpttId(authListRequestDTO.getMcpttId());
        knLogger.info(methodName, "Calling public library for getEmergencyConfigDoc");
        KnEmergencyConfigDocDTO emergencyConfigDoc = pubClientIntf.getEmergencyConfigDoc(ipPubAuthListDTO, persisterTxn);;
        knLogger.info(methodName, "Returned from public  library ", emergencyConfigDoc);
        respDto.setCallOrigMode(emergencyConfigDoc.getCallOrigMode());
        respDto.seteSelMode(emergencyConfigDoc.geteSelMode());
        respDto.seteStateCancelPerm(emergencyConfigDoc.geteStateCancelPerm());
        respDto.seteStateInitPerm(emergencyConfigDoc.geteStateInitPerm());
        respDto.setOrigEmcAlrtInd(emergencyConfigDoc.getOrigEmcAlrtInd());
        respDto.seteLocPollTimer(emergencyConfigDoc.geteLocPollTimer());
        respDto.setEmergConfigTimer(emergencyConfigDoc.getEmergConfigTimer());
        List<KnEmergencyEntryDTO> entryDTOS = new ArrayList<>();
        if(emergencyConfigDoc.getMdnEntry() != null){
        for(KnEmergencyMdnDTO entryDTO : emergencyConfigDoc.getMdnEntry()){
            KnEmergencyEntryDTO entry = new KnEmergencyEntryDTO();
            entry.setEntryMdn(entryDTO.getEntryMdn());
            entry.setPriority(entryDTO.getPriority());
            entry.setType(entryDTO.getType());
            entryDTOS.add(entry);
        }
        }
        respDto.seteList(entryDTOS);
        respDto.setDocEtag(emergencyConfigDoc.getDocEtag());
        knLogger.info(methodName, "response from pub mediator", respDto);
        return respDto;
    }
    /**
     * @param ownerMdn
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public void modifyGroupUriContext(String ownerMdn, String xdmpttserverId, KnPersisterTxn persisterTxn) throws KnException {

        String methodName = "modifyGroupUriContext";
        knLogger.debug( methodName, "ENTRY : Owner mdn - ", ownerMdn == null ? ownerMdn :KnGDPRTemplate.mdn(ownerMdn));
        KnIPPubGroupInfoDTO inputDTO = new KnIPPubGroupInfoDTO();
        inputDTO.setOwner(ownerMdn);
        pubClientIntf.modifyGroupUriContext(inputDTO, xdmpttserverId, persisterTxn);;
        knLogger.debug( methodName, "EXIT:");
    }

    /**
     *
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */

    public KnIPTGSSListDTO getTGSSList(KnXDMTGSSListRequestDTO reqDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException{
        String methodName = "getTGSSList";

        knLogger.debug( methodName, "ENTRY : KnXDMTGSSListRequestDTO- ", reqDTO);
        KnIPTGSSListDTO knIPTGSSListDTO = new KnIPTGSSListDTO();
        knIPTGSSListDTO.setMdn(reqDTO.getMdn());
        knIPTGSSListDTO.setMcpttId(reqDTO.getMcpttId());

        knIPTGSSListDTO = pubClientIntf.getTGSSList(knIPTGSSListDTO,persisterTxn );
        knLogger.debug( methodName, "EXIT:");
        return knIPTGSSListDTO;
    }

    /**
     *
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */

    public KnOpPubResponse updateTGSSList(KnXDMTGSSListRequestDTO reqDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException{

        String methodName = "updateTGSSList";
        KnIPTGSSListDTO knIPTGSSListDTO = new KnIPTGSSListDTO();
        List<Integer> groupIdList = null;
        KnOpPubResponse response = null;
        knLogger.debug( methodName, "ENTRY : KnXDMTGSSListRequestDTO- ", reqDTO);

        groupIdList = new ArrayList<Integer>();
        groupIdList.add(reqDTO.getGroupId());
        knIPTGSSListDTO.setMdn(reqDTO.getMdn());
        knIPTGSSListDTO.setGroupIds(groupIdList);
        knIPTGSSListDTO.setCorpId(reqDTO.getCorpId());
        knIPTGSSListDTO.setMcpttId(reqDTO.getMcpttId());

        response = pubClientIntf.updateTGSSList(knIPTGSSListDTO,persisterTxn );
        knLogger.debug( methodName, "EXIT:");
        return response;
    }

    /**
     *
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse deleteTGSSList(KnXDMTGSSListRequestDTO reqDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "deleteTGSSList";
        knLogger.debug(methodName, "ENTRY : KnXDMTGSSListRequestDTO- ", reqDTO);
        KnIPTGSSListDTO knIPTGSSListDTO = new KnIPTGSSListDTO();
        List<Integer> groupIdList = null;
        KnOpPubResponse response = null;
        groupIdList = new ArrayList<Integer>();
        groupIdList.add(reqDTO.getGroupId());
        knIPTGSSListDTO.setMdn(reqDTO.getMdn());
        knIPTGSSListDTO.setGroupIds(groupIdList);
        knIPTGSSListDTO.setCorpId(reqDTO.getCorpId());
        knIPTGSSListDTO.setMcpttId(reqDTO.getMcpttId());
        response = pubClientIntf.deleteTGSSList(knIPTGSSListDTO, persisterTxn);
        knLogger.debug(methodName, "EXIT:");
        return response;
    }
	
	 public KnMCSXCAPRespDTO getMCPTTUEConfig(KnXDMMcsReqDTO reqDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "getMCPTTUEConfig";
        knLogger.debug( methodName, "--->ENTRY : ");
        KnIPMCSDTO inputDTO = new KnIPMCSDTO();
        inputDTO.setMcId(reqDTO.getMcId());
        inputDTO.setMcpttID(reqDTO.getMcpttID());
        inputDTO.setUserAgent(reqDTO.getUserAgent());
        knLogger.debug( methodName, "--->Library Call : InputDTO:  ", inputDTO);
        KnMCSXCAPRespDTO response = pubClientIntf.getMCPTTUEConfig(inputDTO, persisterTxn);;
        knLogger.debug( methodName, "--->EXIT: ResponseDTO:  ", response);
        return response;
    }

    public KnMCSXCAPRespDTO getMCPTTUserProfile(KnXDMMcsReqDTO reqDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "getMCPTTUserProfile";
        knLogger.debug( methodName, "--->ENTRY : ");
        KnIPMCSDTO inputDTO = new KnIPMCSDTO();
        inputDTO.setMcId(reqDTO.getMcId());
        inputDTO.setMcpttID(reqDTO.getMcpttID());
        inputDTO.setUserAgent(reqDTO.getUserAgent());
        inputDTO.setFileName(reqDTO.getFileName());
        inputDTO.setClientFS2(reqDTO.getClientFS2());
        knLogger.debug( methodName, "--->Library Call : InputDTO:  ", inputDTO);
        KnMCSXCAPRespDTO response = pubClientIntf.getMCPTTUserProfile(inputDTO, persisterTxn);;
        knLogger.debug( methodName, "--->EXIT: ResponseDTO:  ", response);
        return response;
    }

    public KnMCSXCAPRespDTO getMCPTTServiceConfig(KnXDMMcsReqDTO reqDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "getMCPTTServiceConfig";
        knLogger.debug( methodName, "--->ENTRY : ");
        KnIPMCSDTO inputDTO = new KnIPMCSDTO();
        inputDTO.setMcpttID(reqDTO.getMcpttID());
        inputDTO.setUserAgent(reqDTO.getUserAgent());
        knLogger.debug( methodName, "--->Library Call : InputDTO:  ", inputDTO);
        KnMCSXCAPRespDTO response = pubClientIntf.getMCPTTServiceConfig(inputDTO, persisterTxn);;
        knLogger.debug( methodName, "--->EXIT: ResponseDTO:  ", response);
        return response;
    }

    public KnMCSXCAPRespDTO getMCDataUEConfig(KnXDMMcsReqDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCDataUEConfig(KnXDMMcsReqDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : getMCDataUEConfig- ", requestDTO);
        KnMCSXCAPRespDTO respDTO=null;
        KnIPMCSDTO knIPMCSDTO = new KnIPMCSDTO();
        knIPMCSDTO.setMcId(requestDTO.getMcId());
        knIPMCSDTO.setUserAgent(requestDTO.getUserAgent());
        knIPMCSDTO.setMcpttID(requestDTO.getMcpttID());
        knIPMCSDTO.setClientType(requestDTO.getClientType());
        respDTO = pubClientIntf.getMCDataUEConfig(knIPMCSDTO,persisterTxn );
        knLogger.debug( methodName, "EXIT:");
        return respDTO;
    }

    public KnMCSXCAPRespDTO getMCDataUserProfile(KnXDMMcsReqDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCDataUserProfile(KnXDMMcsReqDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : getMCDataUserProfile- ", requestDTO);
        KnMCSXCAPRespDTO respDTO=null;
        KnIPMCSDTO knIPMCSDTO = new KnIPMCSDTO();
        knIPMCSDTO.setMcId(requestDTO.getMcId());
        knIPMCSDTO.setMcpttID(requestDTO.getMcpttID());
        knIPMCSDTO.setUserAgent(requestDTO.getUserAgent());
        knIPMCSDTO.setClientType(requestDTO.getClientType());
        knIPMCSDTO.setFileName(requestDTO.getFileName());
        knIPMCSDTO.setClientFS2(requestDTO.getClientFS2());
        respDTO = pubClientIntf.getMCDataUserProfile(knIPMCSDTO,persisterTxn );
        knLogger.debug( methodName, "EXIT:");
        return respDTO;
    }
    public KnMCSXCAPRespDTO getMCDataServiceConfig(KnXDMMcsReqDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCDataServiceConfig(KnXDMMcsReqDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : getMCDataServiceConfig- ", requestDTO);
        KnMCSXCAPRespDTO respDTO=null;
        KnIPMCSDTO knIPMCSDTO = new KnIPMCSDTO();
        knIPMCSDTO.setMcpttID(requestDTO.getMcpttID());
        knIPMCSDTO.setUserAgent(requestDTO.getUserAgent());
        knIPMCSDTO.setClientType(requestDTO.getClientType());
        respDTO = pubClientIntf.getMCDataServiceConfig(knIPMCSDTO,persisterTxn );
        knLogger.debug( methodName, "EXIT:");
        return respDTO;
    }

    public KnMCSXCAPRespDTO getMCVideoUEConfig(KnXDMMcsReqDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCVideoUEConfig(KnXDMMcsReqDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : getMCVideoUEConfig- ", requestDTO);
        KnMCSXCAPRespDTO respDTO=null;
        KnIPMCSDTO knIPMCSDTO = new KnIPMCSDTO();
        knIPMCSDTO.setMcId(requestDTO.getMcId());
        knIPMCSDTO.setMcpttID(requestDTO.getMcpttID());
        knIPMCSDTO.setUserAgent(requestDTO.getUserAgent());
        knIPMCSDTO.setClientType(requestDTO.getClientType());
        respDTO = pubClientIntf.getMCVideoUEConfig(knIPMCSDTO,persisterTxn );
        knLogger.debug( methodName, "EXIT:");
        return respDTO;
    }

    public KnMCSXCAPRespDTO getMCVideoUserProfile(KnXDMMcsReqDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCVideoUserProfile(KnXDMMcsReqDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : getMCVideoUserProfile- ", requestDTO);
        KnMCSXCAPRespDTO respDTO=null;
        KnIPMCSDTO knIPMCSDTO = new KnIPMCSDTO();
        knIPMCSDTO.setMcId(requestDTO.getMcId());
        knIPMCSDTO.setMcpttID(requestDTO.getMcpttID());
        knIPMCSDTO.setUserAgent(requestDTO.getUserAgent());
        knIPMCSDTO.setClientType(requestDTO.getClientType());
        knIPMCSDTO.setFileName(requestDTO.getFileName());
        knIPMCSDTO.setClientFS2(requestDTO.getClientFS2());
        respDTO = pubClientIntf.getMCVideoUserProfile(knIPMCSDTO,persisterTxn );
        knLogger.debug( methodName, "EXIT:"+respDTO);
        return respDTO;
    }
    public KnMCSXCAPRespDTO getMCVideoServiceConfig(KnXDMMcsReqDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCVideoServiceConfig(KnXDMMcsReqDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : getMCVideoServiceConfig- ", requestDTO);
        KnMCSXCAPRespDTO respDTO=null;
        KnIPMCSDTO knIPMCSDTO = new KnIPMCSDTO();
        knIPMCSDTO.setMcpttID(requestDTO.getMcpttID());
        knIPMCSDTO.setUserAgent(requestDTO.getUserAgent());
        knIPMCSDTO.setClientType(requestDTO.getClientType());
        respDTO = pubClientIntf.getMCVideoServiceConfig(knIPMCSDTO,persisterTxn );
        knLogger.debug( methodName, "EXIT:");
        return respDTO;
    }

    public KnMCSXCAPRespDTO getMCSGroupDoc(KnXDMMcsReqDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCSGroupDoc(KnXDMMcsReqDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY : getMCSGroupDoc- ", requestDTO);
        KnMCSXCAPRespDTO respDTO=null;
        KnIPMCSDTO knIPMCSDTO = new KnIPMCSDTO();
        knIPMCSDTO.setMcpttID(requestDTO.getMcpttID());
        knIPMCSDTO.setCorpID(requestDTO.getCorpID());
        knIPMCSDTO.setGroupID(requestDTO.getGroupID());
        knIPMCSDTO.setGmsFQDN(requestDTO.getGmsFQDN());
        knIPMCSDTO.setUserAgent(requestDTO.getUserAgent());
        knIPMCSDTO.setClientType(requestDTO.getClientType());
        respDTO = pubClientIntf.getMCSGroupDoc(knIPMCSDTO,persisterTxn );
        knLogger.debug( methodName, "EXIT:");
        return respDTO;
    }

    public KnMCSXCAPRespDTO getMCSUserDir(KnXDMMcsReqDTO requestDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getMCSUserDir(KnXDMMcsReqDTO, KnPersisterTxn)";
        knLogger.debug(methodName, "ENTRY :", requestDTO);
        KnMCSXCAPRespDTO respDTO=null;
        KnIPMCSDTO knIPMCSDTO = new KnIPMCSDTO();
        knIPMCSDTO.setMcId(requestDTO.getMcId());
        knIPMCSDTO.setMcpttID(requestDTO.getMcpttID());
        knIPMCSDTO.setCorpID(requestDTO.getCorpID());
        knIPMCSDTO.setGroupID(requestDTO.getGroupID());
        knIPMCSDTO.setUserAgent(requestDTO.getUserAgent());
        knIPMCSDTO.setClientType(requestDTO.getClientType());
        respDTO = pubClientIntf.getMCSUserDir(knIPMCSDTO,persisterTxn );
        knLogger.debug( methodName, "EXIT: respDTO :",respDTO);
        return respDTO;
    }

}
