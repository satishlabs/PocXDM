/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.pubmgmt.clientintf;

import com.kodiak.xdms.server.common.KnXDMServerException;
import com.kodiak.xdms.server.common.dto.clientdat.KnIPChangeMDNInfoDTO;
import com.kodiak.xdms.server.pubmgmt.dto.clientdat.KnIPPubSubsDTO;
import com.kodiak.common.dao.KnPersisterTxn;

/**
 * ************************************************************************
 * <p/>
 * File name:  IPubManager.java
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
public interface IPubManager {


    /**
     *
     * @param subsInfoDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void forceSync(KnIPPubSubsDTO subsInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;


    /**
     *
     * @param subsInfoDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void deleteAllContactsAndGroups(KnIPPubSubsDTO subsInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;


    /**
     *
     * @param changeMdnDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void changeMdn(KnIPChangeMDNInfoDTO changeMdnDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;

    /**
     *
     * @param subsInfoDTO
     * @param persisterTxn
     * @throws KnXDMServerException
     */
    public void deleteAllContactsAndGroups4ListOfMdns(KnIPPubSubsDTO subsInfoDTO, KnPersisterTxn persisterTxn) throws KnXDMServerException;



}
