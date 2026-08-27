/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.clientintf.impl;

import com.kodiak.xdms.server.pubmgmt.clientintf.IPubContactManager;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnOpPubResponse;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubContactInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubContactDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnOPPubDirResponse;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnPubContactInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubContactDTO;
import com.kodiak.xdms.server.pubmgmt.business.IPubContactInfoController;
import com.kodiak.xdms.server.pubmgmt.business.impl.KnPubBORegistry;
import com.kodiak.xdms.server.pubmgmt.resources.KnEntityTypes;
import com.kodiak.xdms.server.pubmgmt.resources.KnOperationTypes;
import com.kodiak.xdms.server.common.framework.KnFWException;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.resources.KnProfileTypes;
import com.kodiak.common.dao.KnPersisterTxn;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  KnPubContactManager.java
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

public class KnPubContactManager implements IPubContactManager {

    /**
     *
     */
    private IPubContactInfoController contactInfoController;


    /**
     *
     */
    KnPubContactManager() {//throws KnFWException {
        contactInfoController = KnPubBORegistry.createPubContactInfoController();
    }


    /**
     *
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse addContacts(KnIPPubContactInfoDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException {

        contactInfo.setEntityId(KnEntityTypes.CONTACT_MANAGER);
        contactInfo.setOperationType(KnOperationTypes.ADD_CONTACT);
        contactInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return contactInfoController.addContacts(contactInfo, persisterTxn);
    }


    /**
     *
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse modifyContacts(KnIPPubContactInfoDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException {

        contactInfo.setEntityId(KnEntityTypes.CONTACT_MANAGER);
        contactInfo.setOperationType(KnOperationTypes.UPDATE_CONTACT);
        contactInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return contactInfoController.modifyContacts(contactInfo, persisterTxn);
    }


    /**
     *
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse deleteContacts(KnIPPubContactInfoDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException {

        contactInfo.setEntityId(KnEntityTypes.CONTACT_MANAGER);
        contactInfo.setOperationType(KnOperationTypes.DELETE_CONTACT);
        contactInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return contactInfoController.deleteContacts(contactInfo, persisterTxn);
    }


    /**
     *
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnPubContactDTO getContactListDetails(KnIPPubContactDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException {

        contactInfo.setEntityId(KnEntityTypes.CONTACT_MANAGER);
        contactInfo.setOperationType(KnOperationTypes.GET_CONTACT_LIST);
        contactInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return contactInfoController.getContactListDetails(contactInfo, persisterTxn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnPubContactDTO getXdmintfContactListDetails(KnIPPubContactDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException{
    	contactInfo.setEntityId(KnEntityTypes.CONTACT_MANAGER);
        contactInfo.setOperationType(KnOperationTypes.GET_XDMINTF_CONTACT_LIST);
        contactInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return contactInfoController.getContactListDetails(contactInfo, persisterTxn);
    }

    /**
     *
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public Collection<KnPubContactInfoDTO> getAllContactLists(KnIPPubContactDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException {

        contactInfo.setEntityId(KnEntityTypes.CONTACT_MANAGER);
        contactInfo.setOperationType(KnOperationTypes.GET_ALL_CONTACT_LISTS);
        contactInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return contactInfoController.getAllContactLists(contactInfo, persisterTxn);
    }

    /**
     *
     * @param contactDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOPPubDirResponse getIndexDetails(KnIPPubContactDTO contactDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException {

        contactDTO.setEntityId(KnEntityTypes.CONTACT_MANAGER);
        contactDTO.setOperationType(KnOperationTypes.GET_DIRECTORY);
        contactDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return contactInfoController.getIndexDetails(contactDTO,  persisterTxn);
    }

    /**
     * Interface to add/modify/remove dynamic contacts
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    @Override
    public KnOpPubResponse modifyDynamicContacts(KnIPPubContactInfoDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        contactInfo.setEntityId(KnEntityTypes.CONTACT_MANAGER);
        contactInfo.setOperationType(KnOperationTypes.MODIFY_DYNAMIC_CONTACT);
        contactInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return contactInfoController.modifyDynamicContacts(contactInfo, persisterTxn);
    }

    /**
     * Interface to delete third party clients' dynamic contacts
     *
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    @Override
    public KnPubContactDTO deleteDynamicContacts(KnIPPubContactInfoDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        contactInfo.setEntityId(KnEntityTypes.CONTACT_MANAGER);
        contactInfo.setOperationType(KnOperationTypes.DELETE_DYNAMIC_CONTACT);
        contactInfo.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return contactInfoController.deleteDynamicContacts(contactInfo, persisterTxn);
    }

    /**
     * Interface to get the dynamic contact list for a third party client.
     *
     * @param inputDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    @Override
    public KnPubContactDTO getDynamicContacts(KnIPPubContactInfoDTO inputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException {
        inputDTO.setEntityId(KnEntityTypes.CONTACT_MANAGER);
        inputDTO.setOperationType(KnOperationTypes.GET_DYNAMIC_CONTACT);
        inputDTO.setProfile(KnProfileTypes.PUBLIC_PROFILE);
        return contactInfoController.getDynamicContacts(inputDTO, persisterTxn);
    }
}
