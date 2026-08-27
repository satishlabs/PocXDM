/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.mediator.helper;

import com.kodiak.common.commdto.common.KnXDMGroupDTO;
import com.kodiak.common.commdto.common.KnXDMGroupMdnInfoDTO;
import com.kodiak.common.commdto.common.KnXDMMdnInfoDTO;
import com.kodiak.common.commdto.request.KnXDMGroupInfoDTO;
import com.kodiak.common.commdto.request.KnXDMSubsInfoDTO;
import com.kodiak.common.commdto.response.KnXDMContactListDetailsDTO;
import com.kodiak.common.commdto.response.KnXDMContactListRespDTO;
import com.kodiak.common.commdto.response.KnXDMGroupDetailsRespDTO;
import com.kodiak.common.commdto.response.KnXDMGroupListRespDTO;
import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants;
import com.kodiak.logger.KnLogger;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.pubmgmt.clientintf.IPubClientIntf;
import com.kodiak.xdms.server.pubmgmt.clientintf.impl.KnPubClientIntf;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubContactDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubGroupDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnGroupMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnMemberDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnPubContactInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnPubGroupInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubGroupDTO;

import java.util.ArrayList;
import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnXDMPubMediator.java
 * Subsystem:  Mediator
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Feb 3, 2011           7.0
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
 * This class supports operations from SOAP wrapper client
 */
public class KnXDMPubMediator {
	private static final KnLogger knLogger = KnLogger.getLogger(KnXDMPubMediator.class);

    private static final String className = KnXDMPubMediator.class.getName();
    IPubClientIntf pubClientIntf = null;
    private static KnXDMPubMediator instance = new KnXDMPubMediator();

    /**
     *
     */
    private KnXDMPubMediator() {
        pubClientIntf = new KnPubClientIntf();
    }

    /**
     * @return
     */
    public static KnXDMPubMediator getInstance() {
        return instance;
    }


    /**
     * @param pubSubsInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMGroupListRespDTO getGroupList(KnXDMSubsInfoDTO pubSubsInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        String methodName = "getGroupList";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubGroupDTO inputDTO = new KnIPPubGroupDTO();
        inputDTO.setOwner(pubSubsInfoDTO.getMdn());
        inputDTO.setClientType(pubSubsInfoDTO.getClientType());
        knLogger.debug( methodName, "Library Call : InputDTO:  " , inputDTO);
        Collection<KnPubGroupDTO> responseDTOs = null;
        if (KnConstants.PTX_XDMDATA_INTF == pubSubsInfoDTO.getClientType()) {
            responseDTOs = pubClientIntf.getPubGroupList(inputDTO, persisterTxn);
        } else {
            responseDTOs = pubClientIntf.getGroupList(inputDTO, persisterTxn);
        }
        knLogger.debug( methodName, "ResponseDTOS:  " , responseDTOs);
        KnXDMGroupListRespDTO xdmRespDTO = new KnXDMGroupListRespDTO();
        Collection<KnXDMGroupDTO> xdmGroupList = new ArrayList<KnXDMGroupDTO>();
        for (KnPubGroupDTO pubGrpDTO : responseDTOs) {
            KnXDMGroupDTO xdmGroupDTO = new KnXDMGroupDTO();
            xdmGroupDTO.setPocGroupId(pubGrpDTO.getGroupId());
            xdmGroupDTO.setGroupDisplayName(pubGrpDTO.getGroupDisplayName());
            xdmGroupDTO.setGroupType(pubGrpDTO.getGroupType());
            xdmGroupDTO.setGroupName(pubGrpDTO.getGroupName());
            xdmGroupList.add(xdmGroupDTO);
        }
        xdmRespDTO.setGroupList(xdmGroupList);
        knLogger.debug( methodName, "EXIT: ResponseDTO:  " , xdmRespDTO);
        return xdmRespDTO;
    }

    /**
     * @param pubGroupInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMGroupDetailsRespDTO getGroupDetails(KnXDMGroupInfoDTO pubGroupInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "getGroupDetails";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubGroupDTO inputDTO = new KnIPPubGroupDTO();
        inputDTO.setOwner(pubGroupInfoDTO.getOwnerMdn());
        inputDTO.setClientType(pubGroupInfoDTO.getClientType());
        inputDTO.setGroupId(pubGroupInfoDTO.getPocGroupId());

        knLogger.debug( methodName, "Library Call : InputDTO:  " , inputDTO);
        KnPubGroupInfoDTO responseDTO = null;
        responseDTO = pubClientIntf.getGroupDetails(inputDTO, persisterTxn);

        knLogger.debug( methodName, "ResponseDTOS:  " , responseDTO);

        // Populate the details response
        KnXDMGroupDetailsRespDTO xdmRespDTO = new KnXDMGroupDetailsRespDTO();
        xdmRespDTO.setGroupDisplayName(responseDTO.getGroupDisplayName());
        xdmRespDTO.setEtag(responseDTO.getGroupDocEtag());
        xdmRespDTO.setGroupType(responseDTO.getGroupType());
        xdmRespDTO.setOwnerMdn(responseDTO.getOwner());
        xdmRespDTO.setMemberCount(responseDTO.getGroupMemberCount());
        KnXDMGroupMdnInfoDTO grpMemberInfo = null;
        Collection<KnGroupMemberDTO> grpMembersList = responseDTO.getGroupMembers();
        ArrayList<KnXDMGroupMdnInfoDTO> groupMdnList = new ArrayList<KnXDMGroupMdnInfoDTO>();
        if (null != grpMembersList && !grpMembersList.isEmpty()) {
            for (KnGroupMemberDTO grpMember : grpMembersList) {
                grpMemberInfo = new KnXDMGroupMdnInfoDTO();
                grpMemberInfo.setMdn(grpMember.getMemberMdn());
                grpMemberInfo.setName(grpMember.getMemberName());
                groupMdnList.add(grpMemberInfo);
            }
        }
        xdmRespDTO.setGrpMembers(groupMdnList);

        knLogger.debug( methodName, "EXIT: ResponseDTO:  " , xdmRespDTO);
        return xdmRespDTO;
    }

    /**
     * @param pubSubsInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnXDMContactListRespDTO getAllContactLists(KnXDMSubsInfoDTO pubSubsInfoDTO, KnPersisterTxn
            persisterTxn) throws KnXDMServerException {

        String methodName = "getAllContactLists";
        knLogger.debug( methodName, "ENTRY : ");
        KnIPPubContactDTO inputDTO = new KnIPPubContactDTO();
        inputDTO.setOwner(pubSubsInfoDTO.getMdn());
        inputDTO.setClientType(pubSubsInfoDTO.getClientType());

        knLogger.debug( methodName, "Library Call : InputDTO:  " , inputDTO);
        Collection<KnPubContactInfoDTO> responseDTOs = pubClientIntf.getAllContactLists(inputDTO, persisterTxn);

        knLogger.debug( methodName, "ResponseDTOS:  " , responseDTOs);

        KnXDMContactListRespDTO medRespDTO = new KnXDMContactListRespDTO();
        Collection<KnXDMContactListDetailsDTO> medRespDetailsDTOs = new ArrayList<KnXDMContactListDetailsDTO>();


        for (KnPubContactInfoDTO responseDTO : responseDTOs) {
            KnXDMContactListDetailsDTO medRespDetailsDTO = new KnXDMContactListDetailsDTO();
            medRespDetailsDTO.setContactListDisplayName(responseDTO.getContactListDisplayName());
            medRespDetailsDTO.setContactListName(responseDTO.getContactListName());

            medRespDetailsDTO.setContactListType(responseDTO.getContactListType());
            medRespDetailsDTO.setOwnerMdn(inputDTO.getOwner());

            Collection<KnMemberDTO> members = responseDTO.getContactMembers();
            if (members != null && !members.isEmpty()) {
                Collection<KnXDMMdnInfoDTO> respMembers = new ArrayList<KnXDMMdnInfoDTO>();
                for (KnMemberDTO member : members) {
                    KnXDMMdnInfoDTO respMember = new KnXDMMdnInfoDTO();
                    respMember.setMdn(member.getMemberMdn());
                    respMember.setName(member.getMemberName());

                    respMembers.add(respMember);
                }
                medRespDetailsDTO.setMembers(respMembers);
            }

            medRespDetailsDTOs.add(medRespDetailsDTO);
        }

        medRespDTO.setContactListDetailsList(medRespDetailsDTOs);

        knLogger.debug( methodName, "EXIT: ResponseDTO:  " , medRespDTO);
        return medRespDTO;
    }


}
