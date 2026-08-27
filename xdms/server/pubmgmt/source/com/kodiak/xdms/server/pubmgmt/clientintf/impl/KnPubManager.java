/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.clientintf.impl;

import com.kodiak.xdms.server.pubmgmt.clientintf.IPubManager;
import com.kodiak.xdms.server.pubmgmt.business.IPubInfoController;
import com.kodiak.xdms.server.pubmgmt.business.impl.KnPubBORegistry;
import com.kodiak.xdms.server.pubmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.pubmgmt.resources.KnOperationTypes;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubSubsDTO;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.xdms.server.common.dto.clientdat.KnIPChangeMDNInfoDTO;
import com.kodiak.common.dao.KnPersisterTxn;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubManager.java
 * Subsystem:  pubmgmt
 * <p/>
 * Name                 	Date         	Release
 * -------------------- ------------------ -------------------------------
 * SureshKumar G        Feb 16, 2011           7.0
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
public class KnPubManager implements IPubManager {

    /**
     *
     */
    private IPubInfoController genericInfoController;

    /**
     *
     */
    KnPubManager() {
        genericInfoController = KnPubBORegistry.createPubInfoController();
    }

    /**
     *
     * @param subsInfoDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void forceSync(KnIPPubSubsDTO subsInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        subsInfoDTO.setEntityId(KnEntityTypes.GENERIC_MANAGER);
        subsInfoDTO.setOperationType(KnOperationTypes.FORCE_SYNC);
        subsInfoDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        genericInfoController.forceSync(subsInfoDTO, persisterTxn);
    }


    /**
     *
     * @param changeMdnDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void changeMdn(KnIPChangeMDNInfoDTO changeMdnDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        changeMdnDTO.setEntityId(KnEntityTypes.GENERIC_MANAGER);
        changeMdnDTO.setOperationType(KnOperationTypes.CHANGE_MDN);
        changeMdnDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        genericInfoController.changeMdn(changeMdnDTO, persisterTxn);
    }


    /**
     *
     * @param subsInfoDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void deleteAllContactsAndGroups(KnIPPubSubsDTO subsInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        subsInfoDTO.setEntityId(KnEntityTypes.GENERIC_MANAGER);
        subsInfoDTO.setOperationType(KnOperationTypes.DELETE_ALL_CONTACTS_AND_GROUPS);
        subsInfoDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        genericInfoController.deleteAllContactsAndGroups(subsInfoDTO, persisterTxn);
    }

    /**
     *
     * @param subsInfoDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void deleteAllContactsAndGroups4ListOfMdns(KnIPPubSubsDTO subsInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {

        subsInfoDTO.setEntityId(KnEntityTypes.GENERIC_MANAGER);
        subsInfoDTO.setOperationType(KnOperationTypes.DELETE_ALL_CONTACTS_AND_GROUPS);
        subsInfoDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        genericInfoController.deleteAllContactsAndGroups4ListOfMdns(subsInfoDTO, persisterTxn);
    }
}
