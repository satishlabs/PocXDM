/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
package com.kodiak.xdms.server.subsmgmt.business;



/**
 * *****************************************************************************
 * File name:   ISubsProvController.java
 * Subsystem:   Subscriber Provisioning Library
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar          09/02/2013       7.4
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
 * *******************************************************************************
 */

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.xdms.server.common.framework.KnFWException;
import com.kodiak.xdms.server.common.framework.validator.KnValidationException;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPPAMAccInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPCreatePAMAccountDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPDeletePAMAccountDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPPAMAccInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPProvDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPUpdatePAMAccountDTO;

import java.util.List;


public interface IPAMAccController {

    /**
     * method to create Subscribers Profile
     *
     * @param pamAccInfoDTO KnIPSubsProvInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPCreateSubsInfoDTO
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException
     *          BO entity Exception
     * @throws com.kodiak.xdms.server.common.framework.KnFWException
     *          Framework Exception
     */
    public KnOPCreatePAMAccountDTO createPAMAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    /**
     * method to delete the Subscribers Profile
     *
     * @param pamAccInfoDTO KnIPSubscriberInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPProvDTO
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException
     *          BO entity Exception
     */
    public KnOPDeletePAMAccountDTO deletePAMAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;


    /**
     * @param pamAccInfoDTO KnIPPAMAccInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPProvDTO
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException
     *
     * @throws com.kodiak.xdms.server.common.framework.KnFWException
     *
     */
    public KnOPProvDTO createPAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    /**
     * @param pamAccInfoDTO KnIPPAMAccInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPProvDTO
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException
     *
     * @throws com.kodiak.xdms.server.common.framework.KnFWException
     *
     */
    public KnOPProvDTO updatePAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    /**
     * @param pamAccInfoDTO KnIPPAMAccInfoDTO
     * @param persisterTxn  KnPersisterTxn
     * @return KnOPProvDTO
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException
     *
     * @throws com.kodiak.xdms.server.common.framework.KnFWException
     *
     */
    public KnOPProvDTO deletePAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;


    public KnOPPAMAccInfoDTO getPAMSubsProfile(KnIPPAMAccInfoDTO pamAccInfo, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPPAMAccInfoDTO retrievePAMSubsProfInfo(KnIPPAMAccInfoDTO pamAccInfo, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPUpdatePAMAccountDTO updatePAMAccState(KnIPPAMAccInfoDTO pamAccInfo, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPPAMAccInfoDTO getPAMAccountInfo(String extPAMAccId, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPPAMAccInfoDTO getPAMAccInfoFromId(int pamAccId, boolean readOnly, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPUpdatePAMAccountDTO updateCorpName(String extCorpId, String corpName, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    public KnOPUpdatePAMAccountDTO updatePAMAccMaxSub(KnIPPAMAccInfoDTO pamAccInfo, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPUpdatePAMAccountDTO updatePAMAccName(KnIPPAMAccInfoDTO pamAccInfo, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPUpdatePAMAccountDTO migratePAMAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public void validateUpgradePAMAccount(int maxSub, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public int retrieveCorporationId(String extCorpId, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPUpdatePAMAccountDTO validateUpdatePamAccount(KnIPPAMAccInfoDTO pamAccInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnValidationException;

    public void updatePAMEtag(int pamAccID, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public List<Integer> retrievePAMAccIdForCorpId(int corpId, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPProvDTO  updateEtagForNNISubscr(String extCorpId,int clientType,KnPersisterTxn persisterTxn)throws KnProvBOException;

}
