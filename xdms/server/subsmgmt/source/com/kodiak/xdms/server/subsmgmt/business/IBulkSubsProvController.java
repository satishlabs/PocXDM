/***************************************************************************************************
 * Copyright Motorola Solutions, Inc. and/or Kodiak Networks, Inc.                                 *
 * All Rights Reserved                                                                             *
 * Motorola Solutions Confidential Restricted                                                      *
 **************************************************************************************************/
/**
 * *****************************************************************************
 * File name:   IBulkSubsProvController.java
 * Subsystem:   Subscriber Provisioning Library
 * <p/>
 * Name                  Date           Release
 * -----------------    -----------     -------
 * Ajit Kumar           feb 18 2013       7.4
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
package com.kodiak.xdms.server.subsmgmt.business;

import com.kodiak.common.dao.KnPersisterTxn;
import com.kodiak.common.resources.KnConstants.HIERARCHY_TYPE;
import com.kodiak.xdms.server.common.framework.KnFWException;
import com.kodiak.xdms.server.subsmgmt.KnProvException;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnIPBulkSubsProvInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPBulkChgAuthStatusRespDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPBulkDeleteSubsRespDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPBulkRespDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPCreateSubsInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPProvDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPSubsProfileInfoDTO;
import com.kodiak.xdms.server.subsmgmt.dto.clientdat.KnOPUpdateSubsInfoDTO;

import java.util.List;
import java.util.Map;

public interface IBulkSubsProvController {
    /**
     * method to create Subscriber Profile
     *
     * @param bulkSubsProvInfoDTO KnIPBulkSubsProvInfoDTO
     * @param persisterTxn        KnPersisterTxn
     * @return KnOPCreateSubsInfoDTO
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException BO entity Exception
     * @throws com.kodiak.xdms.server.common.framework.KnFWException      Framework Exception
     */
    public KnOPCreateSubsInfoDTO createSubscribers(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    /**
     * method to update the Subscriber Profile
     *
     * @param bulkSubsProvInfoDTO KnIPBulkSubsProvInfoDTO
     * @param persisterTxn        KnPersisterTxn
     * @return KnOPCreateSubsInfoDTO
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException BO entity Exception
     * @throws com.kodiak.xdms.server.common.framework.KnFWException      Framework Exception
     */
    public KnOPUpdateSubsInfoDTO updateSubscribers(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    /**
     * method to delete the Subscriber Profile
     *
     * @param bulkSubsProvInfoDTO KnIPBulkSubsProvInfoDTO
     * @param persisterTxn        KnPersisterTxn
     * @return KnOPProvDTO
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException BO entity Exception
     */
    public KnOPBulkDeleteSubsRespDTO deleteSubscribers(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, boolean isLastMDN, KnPersisterTxn persisterTxn) throws KnProvBOException;


    /**
     * method to perform re-activation and de-activation of the subscriber
     *
     * @param bulkSubsProvInfoDTO KnIPBulkSubsProvInfoDTO
     * @param persisterTxn        KnPersisterTxn
     * @return KnOPProvDTO
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException BO entity Exception
     * @throws com.kodiak.xdms.server.common.framework.KnFWException      Framework Exception
     */
    public KnOPBulkChgAuthStatusRespDTO changeServiceAuthStatuses(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException, KnFWException;

    public List<String> retrievePAMAccountMDNs(int pamAccId, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public List<String> getPAMAccountMdnsByInsertionTime(int pamAccId, long insertionTime, KnPersisterTxn persisterTxn) throws KnProvBOException;

    /**
     * method to retrieve the PAM Account MDN Profile Details
     *
     * @param pamAccId     Integer
     * @param persisterTxn KnPersisterTxn
     * @return KnOPSubsProfileInfoDTO List
     * @throws com.kodiak.xdms.server.subsmgmt.business.KnProvBOException BO entity Exception
     */
    public List<KnOPSubsProfileInfoDTO> retrievePAMAccountMDNsDetails(int pamAccId, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public List<String> retrievePAMAccountMDNs(int pamAccId, int fetchSize, String listMdn, KnPersisterTxn persisterTxn) throws KnProvException;

    public List<String> retrievePAMAccountMDNs(int pamAccId, String startMdn, String endMdn, int fetchSize, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPCreateSubsInfoDTO validateCreateSubscriber(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, int subsCount,
                                                          KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public KnOPUpdateSubsInfoDTO validateUpdateSubscriber(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO,
                                                          KnPersisterTxn persisterTxn) throws KnProvException, KnFWException;

    public List<String> retrievePAMAccountProvMDNs(int pamAccId, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public List<String> getPamAccLastSequenceMdns(int pamAccId, int start, int end, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPBulkRespDTO updateDispForSubscribers(Map<String, Integer> subscribers, int bitNo, KnPersisterTxn persisterTxn) throws KnProvException;



    public List<KnOPUpdateSubsInfoDTO> updateHierarchy(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn) throws KnProvBOException;

   /* public List<KnOPProvDTO> sendBulkConfigDocNotification(List<String> mdnList,KnPersisterTxn persisterTxn)throws KnProvBOException;//TODO
*/
    public boolean validateCorpSubsLimitAndPairLimit(int corpId,int subsCount,KnPersisterTxn persisterTxn)throws KnProvBOException;

    public List<String> retrieveBanMDNs(int banId, HIERARCHY_TYPE hierarchy, KnPersisterTxn persisterTxn) throws KnProvException;

    public KnOPProvDTO updatePAMSubsProfCorpId(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO,int oldCorpId, KnPersisterTxn persisterTxn) throws KnProvBOException;

    public KnOPBulkRespDTO updateBulkSubsFSAndPkgIds(KnIPBulkSubsProvInfoDTO bulkSubsProvInfoDTO, KnPersisterTxn persisterTxn)
            throws KnProvBOException, KnFWException;
    }
