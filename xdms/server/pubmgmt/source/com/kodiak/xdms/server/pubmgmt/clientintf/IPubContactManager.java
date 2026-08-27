/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.clientintf;

import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnOpPubResponse;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubContactInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubContactDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnOPPubDirResponse;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnPubContactInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubContactDTO;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.common.dao.KnPersisterTxn;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  IPubContactManager.java
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

public interface IPubContactManager {

    /**
     *
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse addContacts(KnIPPubContactInfoDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;//, KnFWException ;


    /**
     *
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse modifyContacts(KnIPPubContactInfoDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;//, KnFWException ;


    /**
     *
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOpPubResponse deleteContacts(KnIPPubContactInfoDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;//, KnFWException ;


    /**
     *
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnPubContactDTO getContactListDetails(KnIPPubContactDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;//, KnFWException ;
    
    /**
     * This method gets public contact list for request from xdmdataintf.
     * @param contactInfo
     * @param persisterTxn
     * @return KnPubContactDTO
     * @throws KnXDMServerException
     */
    public KnPubContactDTO getXdmintfContactListDetails(KnIPPubContactDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;


    /**
     *
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public Collection<KnPubContactInfoDTO> getAllContactLists(KnIPPubContactDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;//, KnFWException ;

    /**
     *
     * @param contactDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOPPubDirResponse getIndexDetails(KnIPPubContactDTO contactDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;//, KnFWException

    /**
     * Interface to add/modify/remove subscribers dynamic contacts
     * @param inputDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    KnOpPubResponse modifyDynamicContacts(KnIPPubContactInfoDTO inputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    /**
     * Interface to delete third party clients' dynamic contacts
     * @param inputDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    KnPubContactDTO deleteDynamicContacts(KnIPPubContactInfoDTO inputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    /**
     * Interface to get the dynamic contact list for a third party client.
     * @param inputDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    KnPubContactDTO getDynamicContacts(KnIPPubContactInfoDTO inputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;
}
