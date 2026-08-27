/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.clientintf;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.*;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnPubGroupInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubGroupDTO;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.framework.KnFWException;

import java.util.Collection;
import java.util.List;

/**
 * ************************************************************************
 * <p/>
 * File name:  IPubGroupManager.java
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

public interface IPubGroupManager {

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse createGroup(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;//, KnFWException;

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse addGroupMember(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;//, KnFWException;

    /**
     * modify the group member name
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse modifyGroupMember(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;//, KnFWException;

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse modifyGroupName(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;//, KnFWException;

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse deleteGroupMember(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;//, KnFWException;

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse deleteGroup(KnIPPubGroupInfoDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;//, KnFWException;

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnPubGroupInfoDTO getGroupDocDetails(KnIPPubGroupDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;//, KnFWException;

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnPubGroupInfoDTO getPubGroupDetails(KnIPPubGroupDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException; //, KnFWException {


        /**
         *
         * @param groupInfo
         * @param persisterTxn
         * @return
         * @throws KnXDMServerException
         * @throws KnFWException
         */
    public KnPubGroupInfoDTO getGroupDetails(KnIPPubGroupDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;//, KnFWException;

    /**
     * 
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public Collection<KnPubGroupDTO> getGroupList(KnIPPubGroupDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;//, KnFWException;

    /**
     *
     * @param groupInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public Collection<KnPubGroupDTO> getPubGroupList(KnIPPubGroupDTO groupInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    /**
     * Interface to retrieve the dynamic group details
     * @param inputDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    KnPubGroupInfoDTO getDynamicGroupDetails(KnIPPubGroupInfoDTO inputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    /**
     * Interface to retrieve the list of groups associated with an owner MDN.
     * @param inputDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    List<KnPubGroupDTO> getDynamicNonSharedGrpList(KnIPPubGroupInfoDTO inputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    /**
     * Interface to delete the non-shared group
     * @param inputDTO
     * @param persisterTxn
     * @return
     */
    KnOpPubResponse deleteDynamicNonSharedGrp(KnIPPubGroupInfoDTO inputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    /**
     * createNonSharedGroup
     * @param inputDTO
     * @param persisterTxn
     * @return
     */
    KnOpPubResponse createNonSharedGroup(KnIPPubGroupInfoDTO inputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    /**
     * Interface to modify non-shared group details - Add/modify/remove members are supported
     * @param inputDTO
     * @param persisterTxn
     * @return
     */
    KnOpPubResponse modifyNonSharedGroup(KnIPPubGroupInfoDTO inputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    void modifyGroupUriContext(KnIPPubGroupInfoDTO inputDTO, String xdmpttserverId, KnPersisterTxn persisterTxn) throws KnXDMServerException;


}
