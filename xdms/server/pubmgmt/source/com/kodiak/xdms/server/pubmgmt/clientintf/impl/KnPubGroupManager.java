/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.clientintf.impl;

import com.kodiak.xdms.server.pubmgmt.clientintf.IPubGroupManager;
import com.kodiak.xdms.server.pubmgmt.business.IPubGroupInfoController;
import com.kodiak.xdms.server.pubmgmt.business.impl.KnPubBORegistry;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnPubGroupInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubGroupDTO;
import com.kodiak.xdms.server.pubmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.pubmgmt.resources.KnOperationTypes;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.common.dao.KnPersisterTxn;

import java.util.Collection;
import java.util.List;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubGroupManager.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Jan 7, 2011        7.0
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

public class KnPubGroupManager implements IPubGroupManager {

    /**
     * 
     */
    IPubGroupInfoController groupInfoController;


    /**
     *
     */
    KnPubGroupManager() {//throws KnFWException {
        groupInfoController = KnPubBORegistry.createPubGroupInfoController();
    }



    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse createGroup(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        groupInfo.setEntityId(KnEntityTypes.GROUP_MANAGER);
        groupInfo.setOperationType(KnOperationTypes.CREATE_GROUP);
        groupInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return groupInfoController.createGroup(groupInfo,  persisterTxn);
    }


    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse addGroupMember(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        groupInfo.setEntityId(KnEntityTypes.GROUP_MANAGER);
        groupInfo.setOperationType(KnOperationTypes.ADD_GROUP_MEMBER);
        groupInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return groupInfoController.addGroupMember(groupInfo,  persisterTxn);
    }


    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse modifyGroupMember(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        groupInfo.setEntityId(KnEntityTypes.GROUP_MANAGER);
        groupInfo.setOperationType(KnOperationTypes.UPDATE_GROUP_MEMBER);
        groupInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return groupInfoController.modifyGroupMember(groupInfo,  persisterTxn);
    }


    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse modifyGroupName(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        groupInfo.setEntityId(KnEntityTypes.GROUP_MANAGER);
        groupInfo.setOperationType(KnOperationTypes.UPDATE_GROUP_NAME);
        groupInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return groupInfoController.modifyGroupName(groupInfo,  persisterTxn);
    }


    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse deleteGroupMember(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        groupInfo.setEntityId(KnEntityTypes.GROUP_MANAGER);
        groupInfo.setOperationType(KnOperationTypes.DELETE_GROUP_MEMBER);
        groupInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return groupInfoController.deleteGroupMember(groupInfo,  persisterTxn);
    }


    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse deleteGroup(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        groupInfo.setEntityId(KnEntityTypes.GROUP_MANAGER);
        groupInfo.setOperationType(KnOperationTypes.DELETE_GROUP);
        groupInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return groupInfoController.deleteGroup(groupInfo,  persisterTxn);
    }


    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnPubGroupInfoDTO getGroupDocDetails(KnIPPubGroupDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        groupInfo.setEntityId(KnEntityTypes.GROUP_MANAGER);
        groupInfo.setOperationType(KnOperationTypes.GET_GROUP_DOC_DETAILS);
        groupInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return groupInfoController.getGroupDocDetails(groupInfo,  persisterTxn);
    }

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnPubGroupInfoDTO getPubGroupDetails(KnIPPubGroupDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {
        groupInfo.setEntityId(KnEntityTypes.GROUP_MANAGER);
        groupInfo.setOperationType(KnOperationTypes.GET_PUB_GROUP_DETAILS_XDMDATA_INTF);
        groupInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return groupInfoController.getPubGroupDetails(groupInfo, persisterTxn);
    }


    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnPubGroupInfoDTO getGroupDetails(KnIPPubGroupDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        groupInfo.setEntityId(KnEntityTypes.GROUP_MANAGER);
        groupInfo.setOperationType(KnOperationTypes.GET_GROUP_DETAILS);
        groupInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return groupInfoController.getGroupDetails(groupInfo,  persisterTxn);
    }


    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public Collection<KnPubGroupDTO> getGroupList(KnIPPubGroupDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException { //, KnFWException {

        groupInfo.setEntityId(KnEntityTypes.GROUP_MANAGER);
        groupInfo.setOperationType(KnOperationTypes.GET_GROUP_LIST);
        groupInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return groupInfoController.getGroupList(groupInfo,  persisterTxn);
    }

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public Collection<KnPubGroupDTO> getPubGroupList(KnIPPubGroupDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        groupInfo.setEntityId(KnEntityTypes.GROUP_MANAGER);
        groupInfo.setOperationType(KnOperationTypes.GET_PUB_GROUP_LIST);
        groupInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return groupInfoController.getPubGroupList(groupInfo,  persisterTxn);
    }

    /**
     * Interface to retrieve the dynamic group details
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    @Override
    public KnPubGroupInfoDTO getDynamicGroupDetails(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        groupInfo.setEntityId(KnEntityTypes.GROUP_MANAGER);
        groupInfo.setOperationType(KnOperationTypes.GET_DYNAMIC_GROUP_DETAILS);
        groupInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return groupInfoController.getDynamicGroupDetails(groupInfo,  persisterTxn);
    }

    /**
     * Interface to retrieve the list of groups associated with an owner MDN.
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    @Override
    public List<KnPubGroupDTO> getDynamicNonSharedGrpList(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        groupInfo.setEntityId(KnEntityTypes.GROUP_MANAGER);
        groupInfo.setOperationType(KnOperationTypes.GET_DYNAMIC_GROUP_LIST);
        groupInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return groupInfoController.getDynamicNonSharedGrpList(groupInfo,  persisterTxn);
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
        groupInfo.setEntityId(KnEntityTypes.GROUP_MANAGER);
        groupInfo.setOperationType(KnOperationTypes.DELETE_DYNAMIC_NONSHARED_GROUP);
        groupInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return groupInfoController.deleteDynamicNonSharedGrp(groupInfo,  persisterTxn);
    }

    /**
     * createNonSharedGroup
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     */
    @Override
    public KnOpPubResponse createNonSharedGroup(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        groupInfo.setEntityId(KnEntityTypes.GROUP_MANAGER);
        groupInfo.setOperationType(KnOperationTypes.CREATE_DYNAMIC_NONSHARED_GROUP);
        groupInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return groupInfoController.createNonSharedGroup(groupInfo,  persisterTxn);
    }

    /**
     * Interface to modify non-shared group details - Add/modify/remove members are supported
     *
     * @param inputDTO
     * @param persisterTxn
     * @return
     */
    @Override
    public KnOpPubResponse modifyNonSharedGroup(KnIPPubGroupInfoDTO inputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        inputDTO.setEntityId(KnEntityTypes.GROUP_MANAGER);
        inputDTO.setOperationType(KnOperationTypes.MODIFY_DYNAMIC_NONSHARED_GROUP);
        inputDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return groupInfoController.modifyNonSharedGroup(inputDTO, persisterTxn);
    }

    public void modifyGroupUriContext(KnIPPubGroupInfoDTO inputDTO,  String xdmpttserverId, KnPersisterTxn persisterTxn) throws KnXDMServerException{
        inputDTO.setEntityId(KnEntityTypes.GROUP_MANAGER);
        inputDTO.setOperationType(KnOperationTypes.MODIFY_GROUP);
        inputDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        groupInfoController.modifyGroupUriContext(inputDTO,  xdmpttserverId, persisterTxn);
    }

}
