/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.business;

import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnOpPubResponse;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubContactInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubContactDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnOPPubDirResponse;
import com.kodiak.xdms.server.pubmgmt.dto.impl.KnPubContactDTO;
import com.kodiak.xdms.server.pubmgmt.dto.common.KnPubContactInfoDTO;
import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.framework.KnFWException;
import com.kodiak.common.dao.KnPersisterTxn;

import java.util.Collection;

/**
 * ************************************************************************
 * <p/>
 * File name:  IPubContactInfoController.java
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
public interface IPubContactInfoController {


    /**
     *
     * @param ipContactInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse addContacts(KnIPPubContactInfoDTO ipContactInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException;

    /**
     *
     * @param ipContactInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse modifyContacts(KnIPPubContactInfoDTO ipContactInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException;

    /**
     *
     * @param ipContactInfoDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnOpPubResponse deleteContacts(KnIPPubContactInfoDTO ipContactInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException;

    /**
     * 
     * @param ipContactDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     * @throws KnFWException
     */
    public KnPubContactDTO getContactListDetails(KnIPPubContactDTO ipContactDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException, KnFWException;

    /**
     *
     * @param ipContactDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public Collection<KnPubContactInfoDTO> getAllContactLists(KnIPPubContactDTO ipContactDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    /**
     *
     * @param ipContactDTO
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    public KnOPPubDirResponse getIndexDetails(KnIPPubContactDTO ipContactDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    /**
     * Interface to add/modify/remove subscribers dynamic contacts
     * @param contactInfo
     * @param persisterTxn
     * @return
     * @throws KnXDMServerException
     */
    KnOpPubResponse modifyDynamicContacts(KnIPPubContactInfoDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    /**
     * Interface to delete third party clients' dynamic contacts
     * @param contactInfo
     * @param persisterTxn
     * @return
     */
    KnPubContactDTO deleteDynamicContacts(KnIPPubContactInfoDTO contactInfo, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    /**
     * Interface to get the dynamic contact list for third party clients.
     * @param inputDTO
     * @param persisterTxn
     * @return
     */
    KnPubContactDTO getDynamicContacts(KnIPPubContactInfoDTO inputDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;
}
